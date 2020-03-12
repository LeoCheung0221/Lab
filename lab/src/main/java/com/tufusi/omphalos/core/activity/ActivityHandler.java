package com.tufusi.omphalos.core.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.tufusi.lab_annotation.IFindActivity;
import com.tufusi.omphalos.ILabActivity;
import com.tufusi.omphalos.core.LabJsonHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by 鼠夏目 on 2020/3/9.
 *
 * @See
 * @Description InvocationHandler：是proxy代理实例的调用处理程序实现的一个接口
 */
public class ActivityHandler implements InvocationHandler {

    private static final String TAG = "ActivityHandler";

    private Class mILabPointer;
    public Object mActivityProxy;
    private Extend mExtend;

    ActivityHandler(Class iLabPointer) {
        this.mILabPointer = iLabPointer;
        mActivityProxy = Proxy.newProxyInstance(mILabPointer.getClassLoader(), new Class[]{mILabPointer}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //找寻满足的Activity对象标记
        boolean find = false;

        if (ActivityLab.sApplication == null) {
            throw new NullPointerException("Use Lab find Activity need init add Application First!");
        }

        try {
            IFindActivity iFindActivityClzHelper = ActivityLab.generateFindActivity(mILabPointer, method.getName());

            Intent intent = new Intent(ActivityLab.sApplication, iFindActivityClzHelper.targetActivity());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            buildBundle(intent, method, args, iFindActivityClzHelper);

            if (mExtend != null && mExtend.activityWeakReference.get() != null) {
                ((Activity) mExtend.activityWeakReference.get()).startActivityForResult(intent, mExtend.requestCode);
                mExtend.activityWeakReference.clear();
                mExtend = null;
            } else {
                ActivityLab.sApplication.startActivity(intent);
            }

            find = true;
        } catch (Exception e) {
            Log.e(TAG, "invoke error of " + mILabPointer.getName() + ":" + method.getName() + " fail ", e);
        }

        return find;
    }

    private void buildBundle(Intent intent, Method method, Object[] args, IFindActivity iFindActivityClzHelper) {
        if (args == null) {
            return;
        }
        List<String> paramNames = iFindActivityClzHelper.paramNames();
        for (int index = 0; index < args.length; index++) {
            Type type = method.getGenericParameterTypes()[index];
            if (type == Integer.TYPE || type == int.class) {
                intent.putExtra(paramNames.get(index), (Integer) args[index]);
            } else if (type == Byte.TYPE || type == byte.class) {
                intent.putExtra(paramNames.get(index), (Byte) args[index]);
            } else if (type == Short.TYPE || type == short.class) {
                intent.putExtra(paramNames.get(index), (Short) args[index]);
            } else if (type == Long.TYPE || type == long.class) {
                intent.putExtra(paramNames.get(index), (Long) args[index]);
            } else if (type == Float.TYPE || type == float.class) {
                intent.putExtra(paramNames.get(index), (Float) args[index]);
            } else if (type == Double.TYPE || type == double.class) {
                intent.putExtra(paramNames.get(index), (Double) args[index]);
            } else if (type == Boolean.TYPE || type == boolean.class) {
                intent.putExtra(paramNames.get(index), (Boolean) args[index]);
            } else if (type == String.class) {
                intent.putExtra(paramNames.get(index), (String) args[index]);
            } else {
                intent.putExtra(paramNames.get(index), LabJsonHelper.toJson(args[index]));
            }
        }
    }

    public void setExtend(Extend extend) {
        this.mExtend = extend;
    }
}
