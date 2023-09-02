package com.yyxnb.android.secure.utils;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

/**
 * 过滤日志，防止泄露敏感信息
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/8/29
 */
public class LogUtil {

    private static final String TAG = LogUtil.class.getSimpleName();

    /**
     * 数字，英文字母（大小写），中文汉字 正则表达式。
     */
    private static final Pattern M_PATTERN = Pattern.compile("\\d*[a-z|A-Z]*[\u4E00-\u9FA5]*");

    /**
     * 扰码所需的常量.
     */
    private static final char STAR = '*';

    /**
     * 扰码间隔长度.
     */
    private static final int LEN_CONST = 2;

    /**
     * 对要打印的日志进行处理
     *
     * @param msg 需要匿名化的信息
     * @return 匿名化后的信息
     */
    private static String getLogMsg(String msg) {
        StringBuilder retStr = new StringBuilder(512);
        if (!TextUtils.isEmpty(msg)) {
            // 非debug直接匿名
            if (!SecureConfig.getInstance().isDebug()
                    || SecureConfig.getInstance().isLogDesensitization()) {
                retStr.append(formatLogWithStar(msg));
            } else {
                retStr.append(msg);
            }
        }
        return retStr.toString();
    }

    /**
     * DEBUG级别日志输出函数，不匿名化打印
     *
     * @param msg 需输出的消息,允许为null.
     */
    public static void d(String msg) {
        d(TAG, msg);
    }

    /**
     * DEBUG级别日志输出函数，不匿名化打印
     *
     * @param tag 需输出的tag.
     * @param msg 需输出的消息,允许为null.
     */
    public static void d(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (!SecureConfig.getInstance().isDebug()) {
            return;
        }
        Log.d(tag, getLogMsg(msg));
    }

    /**
     * DEBUG级别日志输出函数，不匿名化打印（带异常打印）
     *
     * @param tag 需输出的tag.
     * @param msg 需输出的消息,允许为null.
     * @param e   需要输出的异常堆栈,允许为null.
     */
    public static void d(String tag, String msg, Throwable e) {
        if (TextUtils.isEmpty(msg) && (null == e)) {
            return;
        }
        if (!SecureConfig.getInstance().isDebug()) {
            return;
        }
        Log.d(tag, getLogMsg(msg), getNewThrowable(e));
    }

    /**
     * INFO级别日志输出函数，不匿名化打印
     *
     * @param msg 需输出的消息,允许为null.
     */
    public static void i(String msg) {
        i(TAG, msg);
    }

    /**
     * INFO级别日志输出函数，不匿名化打印
     *
     * @param tag 需输出的tag.
     * @param msg 需输出的消息,允许为null.
     */
    public static void i(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.i(tag, getLogMsg(msg));
    }

    /**
     * INFO级别日志输出函数，不匿名化打印（含异常打印）
     *
     * @param tag 需输出的tag.
     * @param msg 需输出的消息,允许为null.
     * @param e   需要输出的异常堆栈,允许为null.
     */
    public static void i(String tag, String msg, Throwable e) {
        if (TextUtils.isEmpty(msg) && (null == e)) {
            return;
        }
        Log.i(tag, getLogMsg(msg), getNewThrowable(e));
    }

    /**
     * 不匿名化warn打印
     *
     * @param tag 需输出的tag.
     * @param msg 需输出的消息,允许为null.
     */
    public static void w(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.w(tag, getLogMsg(msg));
    }

    /**
     * 不匿名化warn打印
     *
     * @param tag 需输出的tag.
     * @param msg 需输出的消息,允许为null.
     * @param e   需要输出的异常堆栈,允许为null.
     */
    public static void w(String tag, String msg, Throwable e) {
        if (TextUtils.isEmpty(msg) && (null == e)) {
            return;
        }
        Log.w(tag, getLogMsg(msg), getNewThrowable(e));
    }

    /**
     * ERROR 级别日志输出函数，不匿名化打印
     *
     * @param msg 需输出的消息,允许为null.
     */
    public static void e(String msg) {
        e(TAG, msg);
    }

    /**
     * 不匿名化error打印
     *
     * @param e 需要输出的异常堆栈,允许为null.
     */
    public static void e(Throwable e) {
        if (null == e) {
            return;
        }
        Log.e(TAG, "", getNewThrowable(e));
    }

    /**
     * ERROR 级别日志输出函数，不匿名化打印
     *
     * @param tag 需输出的tag.
     * @param msg 需输出的消息,允许为null.
     */
    public static void e(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.e(tag, getLogMsg(msg));
    }

    /**
     * 不匿名化error打印
     *
     * @param tag 需输出的tag.
     * @param msg 需输出的消息,允许为null.
     * @param e   需要输出的异常堆栈,允许为null.
     */
    public static void e(String tag, String msg, Throwable e) {
        if (TextUtils.isEmpty(msg) && (null == e)) {
            return;
        }
        Log.e(tag, getLogMsg(msg), getNewThrowable(e));
    }

    /**
     * 日志匿名化处理，将日志中部分信息用*代替
     * 日志中的中文/英文字母（大小写）/数字（0-9），将交替使用“*”替换
     *
     * @param logStr 需输出的消息,允许为null.
     * @return String 返回匿名化后的信息
     */
    private static String formatLogWithStar(String logStr) {
        try {
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
        } catch (Exception ex) {
            return String.valueOf(STAR);
        }
    }

    /**
     * 获取匿名化后的异常信息
     *
     * @param e Throwable
     * @return Throwable
     */
    private static Throwable getNewThrowable(Throwable e) {
        if (!SecureConfig.getInstance().isDebug()) {
            return e;
        }
        if (e == null) {
            return null;
        } else {
            ThrowableWrapperException retWrapper = new ThrowableWrapperException(e);
            retWrapper.setStackTrace(e.getStackTrace());
            retWrapper.setMessage(modifyExceptionMessage(e.getMessage()));
            ThrowableWrapperException preWrapper = retWrapper;
            // 递归修改cause的message消息
            for (Throwable currThrowable = e.getCause(); currThrowable != null; currThrowable = currThrowable
                    .getCause()) {
                ThrowableWrapperException currWrapper = new ThrowableWrapperException(currThrowable);
                currWrapper.setStackTrace(currThrowable.getStackTrace());
                currWrapper.setMessage(modifyExceptionMessage(currThrowable.getMessage()));
                preWrapper.setCause(currWrapper);
                preWrapper = currWrapper;
            }
            return retWrapper;
        }
    }

    /**
     * 格式化异常信息的message
     *
     * @param message 需要匿名的信息
     * @return 返回匿名后的信息
     */
    private static String modifyExceptionMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            return message;
        } else {
            char[] messageChars = message.toCharArray();
            for (int i = 0; i < messageChars.length; i++) {
                if (i % 2 == 0) {
                    messageChars[i] = '*';
                }
            }
            return new String(messageChars);
        }
    }

    /**
     * 异常包装对象
     */
    private static class ThrowableWrapperException extends Throwable {
        /**
         * 序列化id
         */
        private static final long serialVersionUID = 7129050843360571879L;

        /**
         * 异常消息内容(修改后)
         */
        private String message;

        /**
         * 异常原因
         */
        private Throwable thisCause;

        /**
         * 包装的Throwable对象
         */
        private final Throwable ownerThrowable;

        public ThrowableWrapperException(Throwable t) {
            this.ownerThrowable = t;
        }

        @Override
        public synchronized Throwable getCause() {
            return thisCause == this ? null : thisCause;
        }

        public void setCause(Throwable cause) {
            this.thisCause = cause;
        }

        @Override
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @NonNull
        @Override
        public String toString() {
            if (ownerThrowable == null) {
                return "";
            }
            String throwableClzName = ownerThrowable.getClass().getName();
            if (message != null) {
                String prefix = throwableClzName + ": ";
                if (message.startsWith(prefix)) {
                    return message;
                } else {
                    return prefix + message;
                }
            } else {
                return throwableClzName;
            }
        }
    }
}
