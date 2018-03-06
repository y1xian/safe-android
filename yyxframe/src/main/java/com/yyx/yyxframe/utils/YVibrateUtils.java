package com.yyx.yyxframe.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * 震动
 * 所有方法都要求调用者持有权限
 * <p>All methods requires the caller to hold the permission
 * {@link android.Manifest.permission#VIBRATE}.
 */
public class YVibrateUtils {

    /**
     * 在规定的时间内不断振动。
     * 此方法要求调用方保留权限
     * Vibrate constantly for the specified period of time.
     * <p>This method requires the caller to hold the permission
     * {@link android.Manifest.permission#VIBRATE}.
     *
     * @param milliseconds The number of milliseconds to vibrate.
     */
    public static void vibrate(Context context, long milliseconds) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }

    /**
     * 以给定的模式振动。
     * 在整数的是时间，打开或关闭一个数组传递
     * 振动器以毫秒为单位。第一个值表示毫秒数。
     * 在转动振动器之前等待。下一个值表示毫秒数。
     * 在关闭振动器之前将振动器保持在其上。随后的值替换
     * 在毫秒之间的持续时间，使振动器关闭或打开振动器。
     * < < > > >
     * 要使模式重复，将索引传递到模式数组中
     * 开始重复，或- 1禁用重复。
     * <p>
     * Vibrate with a given pattern.
     * <p/>
     * <p>
     * Pass in an array of ints that are the durations for which to turn on or off
     * the vibrator in milliseconds.  The first value indicates the number of milliseconds
     * to wait before turning the vibrator on.  The next value indicates the number of milliseconds
     * for which to keep the vibrator on before turning it off.  Subsequent values alternate
     * between durations in milliseconds to turn the vibrator off or to turn the vibrator on.
     * </p><p>
     * To cause the pattern to repeat, pass the index into the pattern array at which
     * to start the repeat, or -1 to disable repeating.
     * </p>
     * <p>This method requires the caller to hold the permission
     * {@link android.Manifest.permission#VIBRATE}.
     *
     * @param pattern an array of longs of times for which to turn the vibrator on or off.
     * @param repeat  the index into pattern at which to repeat, or -1 if
     *                you don't want to repeat.
     */
    public static void vibrate(Context context, long[] pattern, int repeat) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, repeat);
    }

}