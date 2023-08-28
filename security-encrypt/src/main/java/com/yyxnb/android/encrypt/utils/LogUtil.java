package com.yyxnb.android.encrypt.utils;

import android.text.TextUtils;
import android.util.Log;

import com.yyxnb.android.encrypt.BuildConfig;

import java.util.regex.Pattern;

/**
 * LogUtil
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/8/29
 */
public class LogUtil {
    /**
     * 数字，英文字母（大小写），中文汉字 正则表达式。
     */
    private static final Pattern M_PATTERN = Pattern.compile("[0-9]*[a-z|A-Z]*[\u4E00-\u9FA5]*");

    /**
     * 扰码所需的常量.
     */
    private static final char STAR = '*';

    private static final String TAG = LogUtil.class.getSimpleName() + ": ";

    /**
     * 扰码间隔长度.
     */
    private static final int LEN_CONST = 2;

    private static String getTag(String tag) {
        return TAG + tag;
    }

    public static void d(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (BuildConfig.DEBUG) {
            Log.d(getTag(tag), msg);
        }
    }

    public static void i(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.i(getTag(tag), getLogMsg(msg, false));
    }

    public static void i(String tag, String msg, boolean isNeedProguard) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.i(getTag(tag), getLogMsg(msg, isNeedProguard));
    }

    public static void e(String tag, String msg, boolean isNeedProguard) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.e(getTag(tag), getLogMsg(msg, isNeedProguard));
    }

    public static void e(String tag, String msg, Throwable throwable) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.e(getTag(tag), msg + " , throwable message : " + throwable.getMessage());
    }

    /**
     * ERROR 级别日志输出函数，不匿名化打印
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.e(getTag(tag), getLogMsg(msg, false));
    }

    /**
     * 对日志进行封装.
     *
     * @param msg            消息体.
     * @param isNeedProguard 是否需要混淆,特指msg.
     * @return String
     */
    private static String getLogMsg(String msg, boolean isNeedProguard) {
        StringBuilder retStr = new StringBuilder(512);
        if (!TextUtils.isEmpty(msg)) {
            if (isNeedProguard) {
                retStr.append(formatLogWithStar(msg));
            } else {
                retStr.append(msg);
            }
        }
        return retStr.toString();
    }

    /**
     * 日志匿名化处理，将日志中部分信息用*代替
     * 日志中的中文/英文字母（大小写）/数字（0-9），将交替使用“*”替换
     *
     * @param logStr
     * @return String
     */
    private static String formatLogWithStar(String logStr) {
        if (TextUtils.isEmpty(logStr)) {
            return logStr;
        }
        final int len = logStr.length();
        if (1 == len) {
            return String.valueOf(STAR);
        }
        StringBuilder retStr = new StringBuilder(len);
        char charAt;
        for (int i = 0, k = 1; i < len; i++) {
            charAt = logStr.charAt(i);
            if (M_PATTERN.matcher(String.valueOf(charAt)).matches()) {
                if (k % LEN_CONST == 0) {
                    charAt = STAR;
                }
                k++;
            }
            retStr.append(charAt);
        }
        return retStr.toString();
    }
}
