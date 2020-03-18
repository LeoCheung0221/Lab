package com.tufusi.lab_annotation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型辅助器
 * 通过反射获取类类型
 */
public class TypeHelper<T> {

    Type tClass = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public Type getType() {
        return tClass;
    }
}
