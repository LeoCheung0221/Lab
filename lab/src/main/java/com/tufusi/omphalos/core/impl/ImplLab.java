package com.tufusi.omphalos.core.impl;

import com.tufusi.lab_annotation.IFindImplClz;
import com.tufusi.omphalos.ILab;
import com.tufusi.omphalos.Lab;
import com.tufusi.omphalos.exception.ErrorCaseHandlerException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description
 */
public class ImplLab {

    private static final String TAG = "ImplLab";
    private static final String IMPL_HELPER_SUFFIX = "ImplHelper";

    private static final int INIT_STATE = -1;
    private static final int CREATED = -2;

    /**
     * try park 100ms
     */
    private static final long THREAD_PARK_NANOS = 50 * 1000000;

    private static Map<Class<? extends ILab>, ILab> sRealImpls = new ConcurrentHashMap<>();
    private static Map<Class, AtomicLong> sImplCtls = new ConcurrentHashMap<>();
    private static Map<Thread, Long> sThreadWaiter = new ConcurrentHashMap<>();

    /**
     * 设置实现ILab接口的对象
     */
    @SuppressWarnings("unchecked")
    private static void putImpl(Class api, ILab impl) {
        if (impl == null) {
            return;
        }
        sRealImpls.put(api, impl);
    }

    /**
     * 获取实现类
     *
     * @param iLab 实现委托代理的接口
     * @param <T>  传递接口
     * @return 返回实现接口的对象
     */
    @SuppressWarnings("unchecked")
    public static <T extends ILab> T getImpl(Class<T> iLab) {
        if (!iLab.isInterface()) {
            Lab.sLabConfig.getILabLog()
                    .error(TAG, String.format("interfaceType must be a interface, %s is not a interface", iLab.getName()), new IllegalArgumentException("interfaceType must be a interface"));
        }

        while (sRealImpls.get(iLab) == null) {
            AtomicLong ctl = getCtls(iLab);
            long currentThreadId = Thread.currentThread().getId();
            if (ctl.compareAndSet(INIT_STATE, currentThreadId)) {
                IFindImplClz iFindImplClzHelper;
                try {
                    iFindImplClzHelper = findImplHelper(iLab);
                    ILab realImpl = (ILab) iFindImplClzHelper.getInstance();
                    realImpl.onInvoke();
                    for (Class apiClass : iFindImplClzHelper.getApis()) {
                        putImpl(apiClass, realImpl);
                    }
                    ctl.set(CREATED);
                    releaseWaiter(iLab);
                } catch (Exception e) {
                    ctl.compareAndSet(currentThreadId, INIT_STATE);
                    Lab.sLabConfig.getILabLog().error(TAG,
                            iLab + " initialization error " + "，Thread：" + Thread.currentThread(), e);
                    return exceptionHandler(iLab);
                }
            } else if (ctl.get() == currentThreadId) {
                Lab.sLabConfig.errorCaseHandler.errorCaseUseLab(
                        "getImpl api:" + iLab + " error,recursion onCreate() on same thread,check api impl " +
                                "init 、 constructor 、onCreate() to avoid circular reference,",
                        ErrorCaseHandlerException.traceToString(2, Thread.currentThread().getStackTrace()));
                break;
            } else if (ctl.get() != CREATED) {
                if (checkDeadLock(iLab, ctl.get())) {
                    return exceptionHandler(iLab);
                } else {
                    addAndParkWaiter(iLab, ctl);
                }
            }

            if (Thread.currentThread().isInterrupted()) {
                Lab.sLabConfig.getILabLog().info(TAG,
                        iLab + " thread  interrupted ,return proxy for the moment" + "，Thread：" +
                                Thread.currentThread());
                return exceptionHandler(iLab);
            }
        }

        return (T) sRealImpls.get(iLab);
    }

    /**
     * 如果其他线程在执行iLab的onInvoke()方法，等待执行完，通过LockSupport.parkNanos()避免CPU的无谓消耗，和一些未考虑的情况下导致的无法终止的park
     *
     * @param iLab           想要获取的接口
     * @param currentWaitFor 当前真正初始化该接口对象的id
     */
    private static void addAndParkWaiter(Class iLab, AtomicLong currentWaitFor) {
        Thread current = Thread.currentThread();
        if (sThreadWaiter.get(current) == null && currentWaitFor.get() != CREATED) {
            sThreadWaiter.put(current, currentWaitFor.get());
            Lab.sLabConfig.getILabLog().info(TAG,
                    "park thread :" + current + ",waiting for threadId on " + currentWaitFor + " to create impl of " +
                            iLab);
            LockSupport.parkNanos(iLab, THREAD_PARK_NANOS);
        }
    }

    /**
     * 检查是否存在不同线程里onCreate循环调用的问题，根据策略让一个线程退出或者让APP抛出崩溃，破坏
     * 等待不释放的条件
     * 避免类似死锁
     *
     * @param iLab           当前线程尝试获取的接口
     * @param currentWaitFor 当前线程等待currentWaitFor线程的初始化
     * @return 是否形成不同线程间的循环初始化
     */
    private static boolean checkDeadLock(Class iLab, long currentWaitFor) {
        Map<Long, Long> checkDeadMap = new HashMap<>(8);
        for (Map.Entry<Thread, Long> threadWait : sThreadWaiter.entrySet()) {
            checkDeadMap.put(threadWait.getKey().getId(), threadWait.getValue());
        }
        Long current = Thread.currentThread().getId();
        Long temp = checkDeadMap.get(current);
        while (temp != null && !temp.equals(current)) {
            temp = checkDeadMap.get(temp);
        }
        boolean deadLock = temp != null;
        if (deadLock) {
            Lab.sLabConfig.getILabLog().info(TAG, current + " leave " + currentWaitFor + "," + iLab);
            //一般是在不同线程初始化互相引用导致
            Lab.sLabConfig.errorCaseHandler.errorCaseUseLab(
                    "getImpl api:" + iLab + " error,recursion on multi thread,check api impl " +
                            "init 、 constructor 、onCreate() to avoid circular reference"
                    , ErrorCaseHandlerException.traceToString(2, Thread.currentThread().getStackTrace()));
        }
        return deadLock;
    }

    @SuppressWarnings("unchecked")
    private static <T extends ILab> T exceptionHandler(Class<T> iLab) {
        if (sRealImpls.get(iLab) == null) {
            return (T) new ImplHandler(iLab).mImplProxy;
        } else {
            return (T) sRealImpls.get(iLab);
        }
    }

    private static void releaseWaiter(Class iLab) {
        Lab.sLabConfig.getILabLog().info(TAG, "releaseWaiter :" + iLab);
        Thread current = Thread.currentThread();
        for (Map.Entry<Thread, Long> lockPair : sThreadWaiter.entrySet()) {

            if (lockPair.getValue() == current.getId()) {
                Lab.sLabConfig.getILabLog().info(TAG,
                        "releaseWaiter :" + lockPair + ",block:" + LockSupport.getBlocker(lockPair.getKey()));
                if (LockSupport.getBlocker(lockPair.getKey()) == iLab) {
                    Lab.sLabConfig.getILabLog().info(TAG, "unPark thread :" + lockPair.getKey());
                    LockSupport.unpark(lockPair.getKey());
                }
                sThreadWaiter.remove(lockPair.getKey());
            }
        }
    }

    private static <T extends ILab> IFindImplClz findImplHelper(Class<T> iLab) throws Exception {
        String apiCanonicalName = iLab.getCanonicalName();

        String packageName = apiCanonicalName.substring(0, apiCanonicalName.lastIndexOf(Lab.PACKAGER_SEPARATOR));

        String apiName = apiCanonicalName.substring(apiCanonicalName.lastIndexOf(Lab.PACKAGER_SEPARATOR) + 1);

        String implCanonicalName =
                packageName + Lab.PACKAGER_SEPARATOR + apiName + Lab.CLASS_NAME_SEPARATOR + IMPL_HELPER_SUFFIX;
        return (IFindImplClz) Class.forName(implCanonicalName).newInstance();
    }

    /**
     * 获取类的原子长整型
     */
    private static AtomicLong getCtls(Class iLab) {
        AtomicLong ctl = sImplCtls.get(iLab);
        if (ctl == null) {
            synchronized (monitor(iLab)) {
                if ((ctl = sImplCtls.get(iLab)) == null) {
                    ctl = new AtomicLong(INIT_STATE);
                    sImplCtls.put(iLab, ctl);
                }
            }
        }
        return ctl;
    }

    private static LabMonitor monitor(Class iLab) {
        int monitorIndex = hash(iLab.hashCode()) & (LabMonitor.values().length - 1);
        return LabMonitor.values()[monitorIndex];
    }

    /**
     * copy from HashMap jdk8
     */
    private static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private enum LabMonitor {
        /**
         * lab class对象公用锁，避免直接锁class可能造成的死锁。length 必须为 2的n次方，用位运算代替求余数
         */
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN
    }

    public static <T extends ILab> boolean implExist(Class<T> iLab) {
        boolean implExist = sRealImpls.containsKey(iLab);

        if (implExist) {
            return true;
        }
        try {
            return findImplHelper(iLab) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
