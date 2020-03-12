package com.tufusi.omphalos.core.activity;

import android.app.Application;

import com.tufusi.lab_annotation.IFindActivity;
import com.tufusi.lab_annotation.LabActivity;
import com.tufusi.omphalos.ILabActivity;
import com.tufusi.omphalos.Lab;

import java.lang.ref.Reference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 鼠夏目 on 2020/3/9.
 *
 * @See
 * @Description
 */
public class ActivityLab {

    //Activity辅助类后缀
    private static final String ACTIVITY_HELPER_SUFFIX = "Helper";
    static volatile Application sApplication;
    //线程安全的HashMap，用于存储Activity辅助类路径
    static Map<String, IFindActivity> sActivityHelperPath = new ConcurrentHashMap<>();
    private static Map<String, ActivityHandler> sActivityProxy = new ConcurrentHashMap<>();

    public static void init(Application application) {
        sApplication = application;
    }

    /**
     * 注入目标类
     */
    public static synchronized void inject(Object target) {
        //获取目标类的注解对象 通过反射获取符合Activity
        LabActivity labActivity = target.getClass().getAnnotation(LabActivity.class);
        IFindActivity iFindActivity = generateFindActivity(labActivity.activityApi(), labActivity.methodName());
        if (iFindActivity != null) {
            iFindActivity.inject(target);
        }
    }

    static IFindActivity generateFindActivity(Class labActivity, String methodName) {
        try {
            //获取规范类名
            String apiCanonicalName = labActivity.getCanonicalName();
            //获取包名
            String packageName = apiCanonicalName.substring(0, apiCanonicalName.lastIndexOf(Lab.PACKAGER_SEPARATOR));
            //获取接口名
            String apiName = apiCanonicalName.substring(apiCanonicalName.lastIndexOf(Lab.PACKAGER_SEPARATOR) + 1);
            //Activity辅助类名
            String activityFindHelperClassName = packageName + Lab.PACKAGER_SEPARATOR + apiName + Lab.CLASS_NAME_SEPARATOR + methodName + Lab.CLASS_NAME_SEPARATOR + ACTIVITY_HELPER_SUFFIX;
            //获取Find类辅助器接口对象
            IFindActivity iFindActivityClzHelper = ActivityLab.sActivityHelperPath.get(activityFindHelperClassName);

            if (iFindActivityClzHelper == null) {
                iFindActivityClzHelper = (IFindActivity) Class.forName(activityFindHelperClassName).newInstance();
            }

            return iFindActivityClzHelper;
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized <T extends ILabActivity> T buildActivity(Class<T> iLabPointer) {
        ActivityHandler activityHandler = getActivityHandler(iLabPointer);

        ActivityLab.sActivityProxy.put(iLabPointer.getCanonicalName(), activityHandler);
        return (T) activityHandler.mActivityProxy;
    }

    /**
     * 构建可拓展的Activity对象
     *
     * @param iLabPointer iLab接口对象指针
     */
    public static synchronized <T extends ILabActivity> Extend<T> buildExtendActivity(Class<T> iLabPointer) {
        ActivityHandler activityHandler = getActivityHandler(iLabPointer);

        Extend<T> expand = new Extend((T) activityHandler.mActivityProxy);
        activityHandler.setExtend(expand);

        ActivityLab.sActivityProxy.put(iLabPointer.getCanonicalName(), activityHandler);
        return expand;

    }

    /**
     * 获取当前Activity的句柄，用于构建Activity
     */
    private static synchronized <T extends ILabActivity> ActivityHandler getActivityHandler(Class<T> iLabPointer) {
        ActivityHandler activityHandler = sActivityProxy.get(iLabPointer.getCanonicalName());
        if (activityHandler == null) {
            activityHandler = new ActivityHandler(iLabPointer);
        }
        return activityHandler;
    }
}
