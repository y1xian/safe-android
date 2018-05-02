package com.yyx.yyxframe.widget.loadingview;


import android.support.annotation.LayoutRes;

import com.yyx.yyxframe.R;


public class YLoadingViewConfig {

    private int emptyViewResId = R.layout.yyx_loading_empty_view;
    private int errorViewResId = R.layout.yyx_loading_error_view;
    private int loadingViewResId = R.layout.yyx_loading_loading_view;
    private int noNetworkViewResId = R.layout.yyx_loading_no_network_view;

    public int getEmptyViewResId() {
        return emptyViewResId;
    }

    public YLoadingViewConfig setEmptyViewResId(@LayoutRes int emptyViewResId) {
        this.emptyViewResId = emptyViewResId;
        return this;
    }

    public int getErrorViewResId() {
        return errorViewResId;
    }

    public YLoadingViewConfig setErrorViewResId(@LayoutRes int errorViewResId) {
        this.errorViewResId = errorViewResId;
        return this;
    }

    public int getLoadingViewResId() {
        return loadingViewResId;
    }

    public YLoadingViewConfig setLoadingViewResId(@LayoutRes int loadingViewResId) {
        this.loadingViewResId = loadingViewResId;
        return this;
    }

    public int getNoNetworkViewResId() {
        return noNetworkViewResId;
    }

    public YLoadingViewConfig setNoNetworkViewResId(@LayoutRes int noNetworkViewResId) {
        this.noNetworkViewResId = noNetworkViewResId;
        return this;
    }
}
