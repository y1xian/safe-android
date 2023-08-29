package com.yyxnb.android.secure.base.string;

import com.yyxnb.android.secure.utils.LogUtil;

/**
 * String类安全封装，防止抛异常导致crash
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/8/30
 */
public class SafeString {

    private static final String TAG = SafeString.class.getSimpleName();
    private static final String EMPTY = "";

    public static String substring(String str, int beginIndex) {
        if (str == null || str.length() < beginIndex || beginIndex < 0) {
            return EMPTY;
        }

        try {
            return str.substring(beginIndex);
        } catch (Exception e) {
            LogUtil.e(TAG, "substring exception: " + e.getMessage());
        }
        return EMPTY;
    }

    public static String substring(String str, int beginIndex, int endIndex) {
        if (str == null || beginIndex < 0 || endIndex > str.length() || endIndex < beginIndex) {
            return EMPTY;
        }
        try {
            return str.substring(beginIndex, endIndex);
        } catch (Exception e) {
            LogUtil.e(TAG, "substring: " + e.getMessage());
        }
        return EMPTY;
    }


    public static String replace(String str, CharSequence target, CharSequence replacement) {
        if (str == null || target == null || replacement == null) {
            return str;
        }
        try {
            return str.replace(target, replacement);
        } catch (Exception e) {
            LogUtil.e(TAG, "replace: " + e.getMessage());
        }
        return str;
    }

}
