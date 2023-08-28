package com.yyxnb.android.activity.protect;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import com.yyxnb.android.utils.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 21-23 保护方法
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public class ActivityProtectV21ToV23 implements IActivityProtect {
	private static final String TAG = ActivityProtectV21ToV23.class.getSimpleName();

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
			LogUtil.e(TAG, "finishStopActivity  exception ");
		}
	}


	private void finish(IBinder binder) throws Exception {

		// http://androidxref.com/5.1.0_r1/xref/frameworks/base/core/java/android/app/ActivityManagerNative.java
		Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
		Method getDefaultMethod = activityManagerNativeClass.getDeclaredMethod("getDefault");
		Object activityManager = getDefaultMethod.invoke(null);

		// http://androidxref.com/5.1.0_r1/xref/frameworks/base/core/java/android/app/IActivityManager.java
		// public boolean finishActivity(IBinder token, int code, Intent data, boolean finishTask)
		Method finishActivityMethod = activityManager.getClass().getDeclaredMethod("finishActivity", IBinder.class, int.class, Intent.class, boolean.class);
		finishActivityMethod.invoke(activityManager, binder, Activity.RESULT_CANCELED, null, false);
	}
}
