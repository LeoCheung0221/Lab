package com.tufusi.omphalos.manager;

import android.util.Log;

import com.tufusi.omphalos.handler.DebugErrorCaseHandler;
import com.tufusi.omphalos.handler.ErrorCaseHandler;
import com.tufusi.omphalos.handler.ReleaseErrorCaseHandler;

/**
 * Created by 鼠夏目 on 2020/3/9.
 *
 * @See
 * @Description 应用基配
 */
public class LabConfig {

    private boolean isDebug;
    private ILabLog iLabLog;

    private static final DebugErrorCaseHandler DEBUG_ERROR_CASE_HANDLER = new DebugErrorCaseHandler();
    private static final ReleaseErrorCaseHandler RELEASE_ERROR_CASE_HANDLER = new ReleaseErrorCaseHandler();
    public ErrorCaseHandler errorCaseHandler = RELEASE_ERROR_CASE_HANDLER;

    /**
     * 私有化构造函数 单例生成 禁止外部私自new对象
     */
    private LabConfig() {
    }

    public static LabConfig create() {
        return new LabConfig();
    }

    public ILabLog getILabLog() {
        if (iLabLog == null) {
            iLabLog = new ILabLog() {
                @Override
                public void info(String tag, String info) {
                    Log.i(tag, info);
                }

                @Override
                public void error(String tag, String msg, Throwable... ex) {
                    Log.e(tag, msg);
                }
            };
        }
        return iLabLog;
    }

    public LabConfig setCoreLog(ILabLog log){
        this.iLabLog = log;
        return this;
    }

    public LabConfig setDebug(boolean debug){
        isDebug = debug;
        if (isDebug){
            errorCaseHandler = DEBUG_ERROR_CASE_HANDLER;
        }else{
            errorCaseHandler = RELEASE_ERROR_CASE_HANDLER;
        }
        return this;
    }

}
