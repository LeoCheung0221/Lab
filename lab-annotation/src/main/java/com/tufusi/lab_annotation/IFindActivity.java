package com.tufusi.lab_annotation;

import java.util.List;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description 找寻类接口
 */
public interface IFindActivity {

    /**
     * 类参
     */
    List<String> paramNames();

    /**
     * 注解函数
     *
     * @param target 目标对象
     */
    void inject(Object target);

    /**
     * 目标Activity
     */
    Class<?> targetActivity();
}
