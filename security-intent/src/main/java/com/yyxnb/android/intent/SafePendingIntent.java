package com.yyxnb.android.intent;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.yyxnb.android.utils.LogUtil;

/**
 * SafePendingIntent
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public final class SafePendingIntent {

	private static final String TAG = SafePendingIntent.class.getSimpleName();

	/**
	 * 第四个参数修改为PendingIntent.FLAG_IMMUTABLE，这样就算截取也不可以修改
	 * PendingIntent. getActivity(context, 0, intent,PendingIntent.FLAG_IMMUTABLE)；
	 *
	 * @param context     上下文对象
	 * @param requestCode 发送者私有的请求码
	 * @param intent      意图对象
	 * @param flag        flag
	 * @return 安全设置后的PendingIntent
	 */

	public static PendingIntent getSafeActivity(Context context, int requestCode, Intent intent, int flag) {
		try {
			return PendingIntent.getActivity(context, requestCode, intent, flag | PendingIntent.FLAG_IMMUTABLE);
		} catch (Throwable e) {
			LogUtil.e(TAG, "PendingIntent getSafeActivity: " + e.getMessage());
			return null;
		}
	}

	/**
	 * 第四个参数修改为PendingIntent.FLAG_IMMUTABLE，这样就算截取也不可以修改
	 *
	 * @param context     上下文对象
	 * @param requestCode 发送者私有的请求码
	 * @param intent      意图对象
	 * @param options     options
	 * @param flag        flag
	 * @return 安全设置后的PendingIntent
	 */
	public static PendingIntent getSafeActivity(Context context, int requestCode, Intent intent, Bundle options,
												int flag) {
		try {
			return PendingIntent.getActivity(context, requestCode, intent, flag | PendingIntent.FLAG_IMMUTABLE,
					options);
		} catch (Throwable e) {
			LogUtil.e(TAG, "PendingIntent getSafeActivity: " + e.getMessage());
			return null;
		}
	}

	/**
	 * 强制修改第四个参数修改为PendingIntent.FLAG_IMMUTABLE
	 *
	 * @param context     上下文对象
	 * @param requestCode 发送者私有的请求码
	 * @param intent      意图对象
	 * @param flag        flag
	 * @return 安全设置后的PendingIntent
	 */
	public static PendingIntent getSafeService(Context context, int requestCode, Intent intent, int flag) {
		try {
			return PendingIntent.getService(context, requestCode, intent, flag | PendingIntent.FLAG_IMMUTABLE);
		} catch (Throwable e) {
			LogUtil.e(TAG, "PendingIntent getSafeService: " + e.getMessage());
			return null;
		}

	}

	/**
	 * 强制修改第四个参数修改为PendingIntent.FLAG_IMMUTABLE
	 *
	 * @param context     上下文对象
	 * @param requestCode 发送者私有的请求码
	 * @param intent      意图对象
	 * @param flag        flag
	 * @return 安全设置后的PendingIntent
	 */
	public static PendingIntent getSafeBroadcast(Context context, int requestCode, Intent intent, int flag) {
		try {
			return PendingIntent.getBroadcast(context, requestCode, intent, flag | PendingIntent.FLAG_IMMUTABLE);
		} catch (Throwable e) {
			LogUtil.e(TAG, "PendingIntent getSafeBroadcast: " + e.getMessage());
			return null;
		}

	}

	/**
	 * 强制修改第四个参数修改为PendingIntent.FLAG_IMMUTABLE
	 *
	 * @param context     上下文对象
	 * @param requestCode 发送者私有的请求码
	 * @param intents     意图对象
	 * @param flag        flag
	 * @return 安全设置后的PendingIntent
	 */
	public static PendingIntent getSafeActivities(Context context, int requestCode, Intent[] intents, int flag) {
		try {
			return PendingIntent.getActivities(context, requestCode, intents, flag | PendingIntent.FLAG_IMMUTABLE);
		} catch (Throwable e) {
			LogUtil.e(TAG, "PendingIntent getSafeActivities: " + e.getMessage());
			return null;
		}
	}

}
