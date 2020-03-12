package com.tufusi.lab_annotation;

import java.util.Set;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description ActivityFind实现类需要实现的接口方法
 */
public interface IFindImplClz {

    /**
     * 获取实例
     */
    Object getInstance();

    /**
     * 获取Api类集合
     */
    Set<Class> getApis();

}
