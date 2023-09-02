package com.yyxnb.android.secure.utils;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

/**
 * 配置项
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/9/3
 */
public class SecureConfig {

    private static volatile SecureConfig sInstance = null;

    private SecureConfig() {
    }

    public static SecureConfig getInstance() {
        if (null == sInstance) {
            synchronized (SecureConfig.class) {
                if (null == sInstance) {
                    sInstance = new SecureConfig();
                }
            }
        }
        return sInstance;
    }

    private Application mApplication;
    private Context mContext;

    /**
     * 是否初始化完成
     */
    private boolean inited = false;
    /**
     * 是否为debug
     */
    private boolean isDebug = true;
    /**
     * 日志是否脱敏处理
     */
    private boolean logDesensitization = false;


    public SecureConfig init(@NonNull Application application) {
        if (!inited || mApplication == null) {
            mApplication = application;
            mContext = application.getApplicationContext();
            inited = true;
        }
        return getInstance();
    }

    public SecureConfig setDebug(boolean debug) {
        isDebug = debug;
        return getInstance();
    }

    public SecureConfig setLogDesensitization(boolean logDesensitization) {
        this.logDesensitization = logDesensitization;
        return getInstance();
    }

    public boolean isLogDesensitization() {
        return logDesensitization;
    }

    public boolean isInited() {
        return inited;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public Context getContext() {
        return mContext;
    }

    public Application getApplication() {
        return mApplication;
    }
}
