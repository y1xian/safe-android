package com.yyxnb.android.activity.protect;

import com.yyxnb.android.utils.LogUtil;

/**
 * 异常处理
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public abstract class ExceptionHandler {
    private static final String TAG = ExceptionHandler.class.getSimpleName();

    public final void uncaughtExceptionHappened(Thread thread, Throwable throwable) {
        LogUtil.i(TAG, "uncaughtExceptionHappened ");
        try {
            // 捕获监听中异常，防止使用方代码抛出异常时导致的反复调用
            onUncaughtExceptionHappened(thread, throwable);
        } catch (Throwable t) {
            LogUtil.e(TAG, "uncaughtExceptionHappened");
        }
    }

    final void bandageExceptionHappened(Throwable throwable) {
        LogUtil.i(TAG, "bandageExceptionHappened ");
        try {
            // 捕获监听中异常，防止使用方代码抛出异常时导致的反复调用
            onBandageExceptionHappened(throwable);
        } catch (Throwable t) {
            LogUtil.e(TAG, "bandageExceptionHappened");
        }
    }

    /**
     * 子线程抛出异常时始终调用该方法。主线程只有第一次抛出异常时才会调用该方法。以后主线程的异常只调用
     * {@link #onBandageExceptionHappened(Throwable)}
     *
     * @param thread
     * @param throwable
     */
    protected abstract void onUncaughtExceptionHappened(Thread thread, Throwable throwable);

    /**
     * 当原本导致app崩溃的主线程异常发生后，主线程再次抛出导致app崩溃异常时会调用该方法。（自己try catch住的异常不会导致app崩溃）
     * 本次异常可能是由于第一次主线程异常时app没有崩溃掉才发生的，只要修复了bug就不会发生该异常了)
     *
     * @param throwable 主线程的异常
     */
    protected abstract void onBandageExceptionHappened(Throwable throwable);

}
