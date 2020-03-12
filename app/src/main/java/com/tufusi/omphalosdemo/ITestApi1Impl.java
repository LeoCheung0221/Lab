package com.tufusi.omphalosdemo;

import android.content.Context;

import com.tufusi.lab_annotation.LabInject;

@LabInject(api = ITestApi1.class)
public class ITestApi1Impl implements ITestApi1 {
    //ITestApi mApi1 = Hub.getImpl(ITestApi.class);
    @Override
    public void test(Context context) {
        //mApi1.test(context);
    }

    @Override
    public void onInvoke() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
