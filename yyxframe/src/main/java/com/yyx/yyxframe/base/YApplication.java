package com.yyx.yyxframe.base;

import android.app.Application;

import com.yyx.yyxframe.YFrame;

/**
 * 基类Application
 * @author yyx
 */
public class YApplication extends Application {
    private static YApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        YFrame.init(this);
    }


    public static YApplication getInstance() {
        return instance;
    }


}
