package com.yyx.yyxframe.utils.log;


import android.text.TextUtils;

import com.yyx.yyxframe.YFrame;


public class YLogConfig {

    private boolean showThreadInfo = true;
    private boolean debug = YFrame.isDebug;
    private String tag = YFrame.tag;


    public YLogConfig setTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            this.tag = tag;
        }
        return this;
    }

    public YLogConfig setShowThreadInfo(boolean showThreadInfo) {
        this.showThreadInfo = showThreadInfo;
        return this;
    }


    public YLogConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isShowThreadInfo() {
        return showThreadInfo;
    }
}
