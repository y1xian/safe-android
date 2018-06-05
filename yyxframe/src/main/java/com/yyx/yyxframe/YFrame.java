package com.yyx.yyxframe;


import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;

import com.yyx.yyxframe.utils.YDensityUtils;
import com.yyx.yyxframe.utils.YOutdatedUtils;
import com.yyx.yyxframe.utils.http.IHttpEngine;
import com.yyx.yyxframe.utils.http.YHttp;
import com.yyx.yyxframe.utils.imageload.ImageLoader;
import com.yyx.yyxframe.utils.imageload.YImage;
import com.yyx.yyxframe.utils.log.YLog;
import com.yyx.yyxframe.utils.log.YLogConfig;

/**
 * 初始化
 */
public class YFrame {
    private static Context context;
    public static int screenHeight;
    public static int screenWidth;

    // #log
    public static String tag = "YFrame";
    public static boolean isDebug = true;


    public static void init(Context context) {
        YFrame.context = context;
        screenHeight = YDensityUtils.getScreenHeight();
        screenWidth = YDensityUtils.getScreenWidth();
    }

    public static YLogConfig initXLog() {
        return YLog.init();
    }

    public static void initXHttp(IHttpEngine httpEngine) {
        YHttp.init(httpEngine);
    }

    public static void initXImageLoader(ImageLoader loader) {
        YImage.init(loader);
    }

    public static Context getContext() {
        synchronized (YFrame.class) {
            if (YFrame.context == null) {
                throw new NullPointerException("Call YFrame.init(context) within your Application onCreate() method." +
                        "Or extends YApplication");
            }
            return YFrame.context.getApplicationContext();
        }
    }

    public static Resources getResources() {
        return YFrame.getContext().getResources();
    }

    public static String getString(@StringRes int id) {
        return getResources().getString(id);
    }

    public static Resources.Theme getTheme() {
        return YFrame.getContext().getTheme();
    }

    public static AssetManager getAssets() {
        return YFrame.getContext().getAssets();
    }

    public static Drawable getDrawable( @DrawableRes int id) {
        return YOutdatedUtils.getDrawable(id);
    }

    public static int getColor( @ColorRes int id) {
        return YOutdatedUtils.getColor(id);
    }

    public static Object getSystemService(String name){
        return YFrame.getContext().getSystemService(name);
    }

    public static Configuration getConfiguration() {
        return YFrame.getResources().getConfiguration();
    }

    public static DisplayMetrics getDisplayMetrics() {
        return YFrame.getResources().getDisplayMetrics();
    }

}
