package com.tufusi.omphalos.manager;

/**
 * Created by 鼠夏目 on 2020/3/9.
 *
 * @See
 * @Description 日志记录接口
 */
public interface ILabLog {

    void info(String tag, String info);

    void error(String tag, String msg, Throwable... ex);

}
