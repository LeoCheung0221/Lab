package com.tufusi.omphalosdemo;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tufusi.lab_annotation.LabActivity;
import com.tufusi.omphalos.Lab;

import java.util.List;
import java.util.Map;

@LabActivity(activityApi = IActivityTest.class, methodName = "activitySecond")
public class SecondActivity extends AppCompatActivity {
    List<Map<String, Integer>> listMap;
    int value;
    TextView mContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Lab.inject(this);

        mContent = findViewById(R.id.content);
        mContent.setText("second Activity \n process :second \n a size " + listMap.size() + " , b = " + value);

        Log.d("SecondActivity", "a = " + listMap + "b =" + listMap);
    }
}
