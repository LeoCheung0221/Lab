package com.tufusi.omphalos.handler;

import com.tufusi.omphalos.Lab;

/**
 * Created by 鼠夏目 on 2020/3/9.
 *
 * @See
 * @Description
 */
public class ReleaseErrorCaseHandler implements ErrorCaseHandler {

    @Override
    public void errorCaseUseLab(String msg, String throwableStackLog) {
        Lab.sLabConfig.getILabLog().error("ReleaseErrorCaseHandler", msg + "\n" + throwableStackLog);
    }
}
