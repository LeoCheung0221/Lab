package com.tufusi.lab_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 鼠夏目
 * @date 2020/3/10
 * @See
 * @Description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface LabInject {

    /**
     * 注解api函数
     *
     * @return 注解对象类数组
     */
    Class[] api();
}
