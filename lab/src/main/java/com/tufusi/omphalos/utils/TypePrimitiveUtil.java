package com.tufusi.omphalos.utils;

/**
 * Created by 鼠夏目 on 2020/3/10.
 *
 * @See
 * @Description
 */
public class TypePrimitiveUtil {

    /**
     * 是否是基本类型判断
     *
     * @param type 类
     * @return 返回布尔值
     */
    public static boolean isPrimitiveType(Class<?> type) {
        return type == byte.class
                || type == int.class
                || type == short.class
                || type == float.class
                || type == double.class
                || type == long.class;
    }
}
