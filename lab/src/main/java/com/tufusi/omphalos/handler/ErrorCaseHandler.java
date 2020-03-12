package com.tufusi.omphalos.handler;

/**
 * Created by 鼠夏目 on 2020/3/9.
 *
 * @See
 * @Description 错误异常处理接口
 */
public interface ErrorCaseHandler {

    /**
     * 使用基类库错误异常接口
     */
    void errorCaseUseLab(String msg, String throwableStackLog);

}
