package com.yyxnb.android.activity.protect;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import com.yyxnb.android.utils.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ActivityProtectV24ToV25
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public class ActivityProtectV24ToV25 implements IActivityProtect {

	/**
	 * 24-25 保护方法 ActivityManagerNative.getDefault().finishActivity(mToken, resultCode, resultData, finishTask))
	 */
	private static final String TAG = ActivityProtectV24ToV25.class.getSimpleName();

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
		finishSomeArgs(message);
	}


	@Override
	public void finishPauseActivity(Message message) {
		LogUtil.i(TAG, "finishPauseActivity: ");
		finishSomeArgs(message);
	}

	@Override
	public void finishStopActivity(Message message) {
		LogUtil.i(TAG, "finishStopActivity: ");
		finishSomeArgs(message);
	}


	private void finishSomeArgs(Message message) {
		try {
			Object someArgs = message.obj;
			Field arg1Field = someArgs.getClass().getDeclaredField("arg1");
			arg1Field.setAccessible(true);
			IBinder binder = (IBinder) arg1Field.get(someArgs);
			finish(binder);
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "finishSomeArgs exception  ");
		}
	}

	private void finish(IBinder binder) throws Exception {

		// http://androidxref.com/6.0.1_r10/xref/frameworks/base/core/java/android/app/ActivityManagerNative.java
		Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
		Method getDefaultMethod = activityManagerNativeClass.getDeclaredMethod("getDefault");
		Object activityManager = getDefaultMethod.invoke(null);

		// http://androidxref.com/6.0.1_r10/xref/frameworks/base/core/java/android/app/IActivityManager.java
		Method finishActivityMethod = activityManager.getClass()
				.getDeclaredMethod("finishActivity", IBinder.class, int.class, Intent.class, int.class);
		int dontFinishTaskWithActivity = 0;
		finishActivityMethod.invoke(activityManager, binder, Activity.RESULT_CANCELED, null, dontFinishTaskWithActivity);
	}
}
