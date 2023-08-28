package com.yyxnb.android.activity.protect;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.servertransaction.ClientTransaction;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import com.yyxnb.android.utils.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ActivityProtectV28ToV29
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public class ActivityProtectV28ToV29 implements IActivityProtect {

	private static final String TAG = ActivityProtectV28ToV29.class.getSimpleName();

	@Override
	public void finishLaunchActivity(Message message) {
		LogUtil.i(TAG, "finishLaunchActivity: ");
		try {
			tryFinish1(message);
			return;
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "finishLaunchActivity1 exception ");
		}

		try {
			tryFinish2(message);
			return;
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "finishLaunchActivity2 exception ");
		}

		try {
			tryFinish3(message);
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "finishLaunchActivity3 exception ");
		}
	}

	private void tryFinish1(Message message) throws Throwable {
		ClientTransaction clientTransaction = (ClientTransaction) message.obj;
		IBinder binder = clientTransaction.getActivityToken();
		finish(binder);
	}

	private void tryFinish3(Message message) throws Throwable {
		Object clientTransaction = message.obj;
		Field mActivityTokenField = clientTransaction.getClass().getDeclaredField("mActivityToken");
		IBinder binder = (IBinder) mActivityTokenField.get(clientTransaction);
		finish(binder);
	}

	private void tryFinish2(Message message) throws Throwable {
		Object clientTransaction = message.obj;
		Method getActivityTokenMethod = clientTransaction.getClass().getDeclaredMethod("getActivityToken");
		IBinder binder = (IBinder) getActivityTokenMethod.invoke(clientTransaction);
		finish(binder);
	}


	@Override
	public void finishResumeActivity(Message message) {

	}


	@Override
	public void finishPauseActivity(Message message) {

	}

	@Override
	public void finishStopActivity(Message message) {
	}

	private void finish(IBinder binder) throws Exception {
		// http://androidxref.com/9.0.0_r3/xref/frameworks/base/core/java/android/app/ActivityManager.java#getService
		Method getServiceMethod = ActivityManager.class.getDeclaredMethod("getService");
		Object activityManager = getServiceMethod.invoke(null);

		// http://androidxref.com/9.0.0_r3/xref/frameworks/base/core/java/android/app/IActivityManager.aidl#finishActivity
		Method finishActivityMethod = activityManager.getClass().getDeclaredMethod("finishActivity", IBinder.class, int.class, Intent.class, int.class);
		finishActivityMethod.setAccessible(true);
		int dontFinishTaskWithActivity = 0;
		finishActivityMethod.invoke(activityManager, binder, Activity.RESULT_CANCELED, null, dontFinishTaskWithActivity);

	}
}
