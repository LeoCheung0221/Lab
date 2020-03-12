package com.tufusi.omphalos.core.impl;

import android.util.Log;

import com.tufusi.omphalos.BuildConfig;
import com.tufusi.omphalos.utils.TypePrimitiveUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description 实现proxy代理实例的调用处理程序接口
 */
public class ImplHandler implements InvocationHandler {

    private static final String TAG = "ImplHandler";

    public Object mImplProxy;
    private Class mILab;

    public ImplHandler(Class iLab) {
        this.mILab = iLab;
        this.mImplProxy = Proxy.newProxyInstance(mILab.getClassLoader(), new Class[]{mILab}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.e(TAG, "invoke impl of " + mILab.getName() + " fail, impl not exit!");

        /*
          debug模式抛出崩溃
         */
        if (method.getReturnType() != Void.TYPE && BuildConfig.DEBUG) {
            throw new RuntimeException("can't get return value, no impl of " + mILab.getName());
        }

        if(TypePrimitiveUtil.isPrimitiveType(method.getReturnType())) {
            return -1;
        } else if(method.getReturnType() == char.class){
            return '0';
        }else if(method.getReturnType() == boolean.class){
            return false;
        }

        return null;
    }
}
