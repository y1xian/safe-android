package com.yyxnb.android.secure.base.string;

import android.text.TextUtils;

import com.yyxnb.android.secure.utils.LogUtil;

/**
 * SafeStringBuilder
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/8/30
 */
public class SafeStringBuilder {
    private static final String TAG = SafeStringBuilder.class.getSimpleName();
    private static final String EMPTY = "";

    public static String substring(StringBuilder str, int beginIndex) {
        if (TextUtils.isEmpty(str) || str.length() < beginIndex || beginIndex < 0) {
            return EMPTY;
        }

        try {
            return str.substring(beginIndex);
        } catch (Exception e) {
            LogUtil.e(TAG, "substring exception: " + e.getMessage());
        }
        return EMPTY;
    }

    public static String substring(StringBuilder str, int beginIndex, int endIndex) {
        if (TextUtils.isEmpty(str) || beginIndex < 0 || endIndex > str.length() || endIndex < beginIndex) {
            return EMPTY;
        }
        try {
            return str.substring(beginIndex, endIndex);
        } catch (Exception e) {
            LogUtil.e(TAG, "substring: " + e.getMessage());
        }
        return EMPTY;
    }
}
