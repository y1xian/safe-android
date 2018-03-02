package com.yyx.yyxframe.base;

/**
 * 基类接口
 */
public interface ICallback {
    //返回布局文件id
    int layoutResID();
    //初始化布局文件
    void initView();
    //初始化数据
    void initData();
}
