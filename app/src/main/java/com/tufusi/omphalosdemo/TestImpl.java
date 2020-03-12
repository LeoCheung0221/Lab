package com.tufusi.omphalosdemo;

import android.content.Context;
import android.widget.Toast;

import com.tufusi.lab_annotation.LabInject;

@LabInject(api = {IMultiApi.class, ITestApi.class})
class TestImpl implements IMultiApi, ITestApi {

    @Override
    public void test(Context context) {
        Toast.makeText(context, "TestImpl 的 test方法被调用", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInvoke() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showMulti(Context context) {
        Toast.makeText(context, "TestImpl 的 showMulti方法被调用", Toast.LENGTH_LONG).show();
    }
}
