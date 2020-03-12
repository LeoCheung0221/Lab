package com.tufusi.omphalosdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tufusi.omphalos.Lab;
import com.tufusi.omphalos.manager.LabConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ImplHub";
    private Thread mThread1;
    private Thread mThread2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Lab.configLab(LabConfig.create().setDebug(false));

        findViewById(R.id.method_invoke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long start = System.currentTimeMillis();
                        ARouter.getInstance().navigation(ARouterSever1.class);
                        Log.d("App", "ARouterSever1 coast" + (System.currentTimeMillis() - start));
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long start = System.currentTimeMillis();
                        ARouter.getInstance().navigation(ARouterSever2.class);
                        Log.d("App", "ARouterSever2 coast" + (System.currentTimeMillis() - start));

                    }
                }).start();

                //hub
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long start = System.currentTimeMillis();
                        Lab.getImpl(ITestApi.class);
                        Log.d("App", "ITestApi coast" + (System.currentTimeMillis() - start));
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long start = System.currentTimeMillis();
                        Lab.getImpl(ITestApi1.class);
                        Log.d("App", "ITestApi1 coast" + (System.currentTimeMillis() - start));

                    }
                }).start();

            }
        });

        findViewById(R.id.activity_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Lab.getImpl(ITestApi.class).test(MainActivity.this);
//                Lab.getImpl(ITestApi1.class).test(MainActivity.this);

                List<Map<String, Integer>> mapList = new ArrayList<>();
                Map a = new HashMap<String, Integer>();
                a.put("abc", 10);
                Map b = new HashMap<String, Integer>();
                b.put("abc", 3);
                b.put("278", 5);
                mapList.add(a);
                mapList.add(b);
                boolean found = Lab.getActivity(IActivityTest.class).activitySecond(mapList, 9);
                Toast.makeText(MainActivity.this, "找到了对应的Activity ? " + found, Toast.LENGTH_LONG).show();
                Lab.getActivityWithExtend(IActivityTest.class).withResult(MainActivity.this, 10).build().activitySecond(mapList, 9);

//                Lab.getActivity(ITestApi.class).activitySecond("Hello", 10);
//                Lab.getActivityWithExtend(ITestApi.class).build().activitySecond("Hello", 10);
//                Lab.getActivityWithExtend(ITestApi.class).withResult(MainActivity.this, 5).build().activitySecond("Hello", 10);
            }
        });

        if (Lab.implExist(NoImplApi.class)) {
            Lab.getImpl(NoImplApi.class).noReturnImpl();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
