package com.tufusi.omphalos;

import android.app.Application;

import com.tufusi.omphalos.core.activity.ActivityLab;
import com.tufusi.omphalos.core.activity.Extend;
import com.tufusi.omphalos.core.impl.ImplLab;
import com.tufusi.omphalos.manager.LabConfig;

/**
 * Created by 鼠夏目 on 2020/3/9.
 *
 * @See
 * @Description 当需要通过接口调用实现时，可以避免检查空值的库
 */
public class Lab {

    //包名类名 分隔符标记
    public static final String PACKAGER_SEPARATOR = ".";
    public static final String CLASS_NAME_SEPARATOR = "_";
    public static LabConfig sLabConfig = LabConfig.create();

    private Lab() {
    }

    /**
     * 初始化库
     */
    public static void init(Application application) {
        ActivityLab.init(application);
    }

    /**
     * 反转依赖注入目标对象
     *
     * @param targetObj 目标对象
     */
    public static void inject(Object targetObj) {
        ActivityLab.inject(targetObj);
    }

    /**
     * 配置Lab
     */
    public static void configLab(LabConfig labConfig) {
        sLabConfig = labConfig;
    }

    /**
     * 获取实现类
     * 返回泛型类 实现ILab接口
     */
    public static <T extends ILab> T getImpl(Class<T> iLab) {
        return ImplLab.getImpl(iLab);
    }

    public static  <T extends ILabActivity> T getActivity(Class<T> iLabPointer) {
        return ActivityLab.buildActivity(iLabPointer);
    }

    public static <T extends ILabActivity> Extend<T> getActivityWithExtend(Class<T> iLabPointer) {
        return ActivityLab.buildExtendActivity(iLabPointer);
    }

    public static  <T extends ILab> boolean implExist(Class<T> iLab) {
        return ImplLab.implExist(iLab);
    }
}
