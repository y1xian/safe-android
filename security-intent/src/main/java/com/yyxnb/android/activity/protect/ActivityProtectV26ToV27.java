package com.yyxnb.android.activity.protect;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import com.yyxnb.android.encrypt.utils.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * handleDestroyActivity((IBinder)msg.obj, msg.arg1 != 0,msg.arg2, false);
 * ActivityManager.getService().finishActivity(mToken, resultCode, resultData, finishTask)
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public class ActivityProtectV26ToV27 implements IActivityProtect {

	private static final String TAG = ActivityProtectV26ToV27.class.getSimpleName();

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
			LogUtil.e(TAG, "finishLaunchActivity exception ");
		}
	}

	private void finish(IBinder binder) throws Exception {
		// http://androidxref.com/8.1.0_r33/xref/frameworks/base/core/java/android/app/ActivityManager.java#getService
		Method getServiceMethod = ActivityManager.class.getDeclaredMethod("getService");
		Object activityManager = getServiceMethod.invoke(null);
		// http://androidxref.com/8.1.0_r33/xref/frameworks/base/core/java/android/app/ActivityManager.java#finishActivity
		Method finishActivityMethod = activityManager.getClass().getDeclaredMethod("finishActivity", IBinder.class, int.class, Intent.class, int.class);
		finishActivityMethod.setAccessible(true);
		int dontFinishTaskWithActivity = 0;
		finishActivityMethod.invoke(activityManager, binder, Activity.RESULT_CANCELED, null, dontFinishTaskWithActivity);
	}
}
