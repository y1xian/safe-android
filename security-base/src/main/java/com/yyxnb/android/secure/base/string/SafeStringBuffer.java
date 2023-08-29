package com.yyxnb.android.secure.base.string;

import com.yyxnb.android.secure.utils.LogUtil;

/**
 * SafeStringBuffer
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/8/30
 */
public class SafeStringBuffer {
    private static final String TAG = SafeStringBuffer.class.getSimpleName();
    private static final String EMPTY = "";

    public static String substring(StringBuffer str, int beginIndex) {
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

    public static String substring(StringBuffer str, int beginIndex, int endIndex) {
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
}
