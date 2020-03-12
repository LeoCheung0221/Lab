package com.tufusi.omphalos.handler;

import com.tufusi.omphalos.exception.ErrorCaseHandlerException;

/**
 * Created by 鼠夏目 on 2020/3/9.
 *
 * @See
 * @Description 调试异常情形下的处理
 */
public class DebugErrorCaseHandler extends ReleaseErrorCaseHandler{


    @Override
    public void errorCaseUseLab(String msg, String throwableStackLog) {
        super.errorCaseUseLab(msg, throwableStackLog);
        throw new ErrorCaseHandlerException(msg);
    }
}
