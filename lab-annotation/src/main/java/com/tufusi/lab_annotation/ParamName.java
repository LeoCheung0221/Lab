package com.tufusi.lab_annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description 将其标记为参数 如果值为默认值，则使用原始参数名
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ParamName {
}
