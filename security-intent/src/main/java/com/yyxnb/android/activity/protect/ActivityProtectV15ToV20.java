package com.yyxnb.android.activity.protect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import com.yyxnb.android.utils.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 15-20 保护
 * http://androidxref.com/4.4.4_r1/xref/frameworks/base/core/java/android/app/ActivityManagerNative.java
 * http://androidxref.com/4.4.4_r1/xref/frameworks/base/core/java/android/app/IActivityManager.java
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public class ActivityProtectV15ToV20 implements IActivityProtect {

	private static final String TAG = ActivityProtectV15ToV20.class.getSimpleName();

	@Override
	public void finishLaunchActivity(Message message) {
		LogUtil.i(TAG, "finishLaunchActivity: ");
		try {
			Object activityClientRecord = message.obj;
			final Field tokenField = activityClientRecord.getClass().getDeclaredField("token");
			tokenField.setAccessible(true);
			IBinder binder = (IBinder) tokenField.get(activityClientRecord);
			finish(binder);
		} catch (Exception e) {
			LogUtil.e(TAG, "finishLaunchActivity exception ");
		}
	}

	@Override
	public void finishResumeActivity(Message message) {
		LogUtil.i(TAG, "finishResumeActivity: ");
		try {
			finish((IBinder) message.obj);
		} catch (Exception e) {
			LogUtil.e(TAG, "finishResumeActivity exception ");
		}
	}

	@Override
	public void finishPauseActivity(Message message) {
		LogUtil.i(TAG, "finishPauseActivity: ");
		try {
			finish((IBinder) message.obj);
		} catch (Exception e) {
			LogUtil.e(TAG, "finishPauseActivity exception ");
		}
	}

	@Override
	public void finishStopActivity(Message message) {
		LogUtil.i(TAG, "finishStopActivity: ");
		try {
			finish((IBinder) message.obj);
		} catch (Exception e) {
			LogUtil.e(TAG, "finishStopActivity exception ");
		}
	}

	@SuppressLint("PrivateApi")
	private void finish(IBinder binder) throws Exception {

		// http://androidxref.com/4.4.4_r1/xref/frameworks/base/core/java/android/app/ActivityManagerNative.java
		Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
		// 返回 IActivityManager ，
		// http://androidxref.com/4.4.4_r1/xref/frameworks/base/core/java/android/app/IActivityManager.java
		Method getDefaultMethod = activityManagerNativeClass.getDeclaredMethod("getDefault");
		Object activityManager = getDefaultMethod.invoke(null);

		// 调用 public boolean finishActivity(IBinder token, int code, Intent data);
		Method finishActivityMethod = activityManager.getClass().getDeclaredMethod("finishActivity", IBinder.class,
				int.class, Intent.class);
		finishActivityMethod.invoke(activityManager, binder, Activity.RESULT_CANCELED, null);

	}
}
