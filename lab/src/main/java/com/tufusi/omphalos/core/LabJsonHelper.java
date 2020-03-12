package com.tufusi.omphalos.core;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description
 */
public class LabJsonHelper {

    public static String toJson(Object object) {
        return JSON.toJSONString(object);
    }

    public static <T> T fromJson(String string, Type type) {
        try {
            return JSON.parseObject(string,type);
        } catch (Exception e) {
            return null;
        }
    }
}
