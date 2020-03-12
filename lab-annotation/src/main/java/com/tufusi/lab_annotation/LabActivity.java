package com.tufusi.lab_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LabActivity {
    Class activityApi();
    String methodName();
}
