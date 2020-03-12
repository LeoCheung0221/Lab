package com.tufusi.omphalosdemo;

import com.tufusi.omphalos.ILabActivity;

import java.util.List;
import java.util.Map;

public interface IActivityTest extends ILabActivity {
    boolean activitySecond(List<Map<String, Integer>> listMap, int value);
    void c();
}
