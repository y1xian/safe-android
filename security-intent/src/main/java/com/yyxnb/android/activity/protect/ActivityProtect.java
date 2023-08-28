package com.yyxnb.android.activity.protect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.yyxnb.android.utils.LogUtil;

import java.lang.reflect.Field;

/**
 * Activity保护类,通过替换ActivityThread.mH.mCallback，实现拦截Activity生命周期，
 * 通过调用ActivityManager的finishActivity结束掉生命周期抛出异常的Activity
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public final class ActivityProtect {
	private static boolean isShowLog = false;
	private static final String TAG = ActivityProtect.class.getSimpleName();
	private static IActivityProtect iActivityProtect;
	private static ExceptionHandler sExceptionHandler;
	private static boolean isInited = false; // 标记位，避免重复安装卸载
	private static boolean sIsSafeMode;

	private ActivityProtect() {
	}

	public static void init(Context ctx, ExceptionHandler exceptionHandler) {
		if (isInited) {
			return;
		}
		try {
			// 解除 android P 反射限制
			Reflection.unseal(ctx);
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "un reflect error :" + throwable.getMessage(), throwable);
		}
		isInited = true;
		sExceptionHandler = exceptionHandler;

		initActivityProtect();
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			if (sExceptionHandler != null) {
				sExceptionHandler.uncaughtExceptionHappened(thread, throwable);
			}

			if (thread == Looper.getMainLooper().getThread()) {
				isChoreographerException(throwable);
				isThreadGroupUncaughtException(throwable);
				safeMode();
			}
		});
	}

	/**
	 * 替换ActivityThread.mH.mCallback，实现拦截Activity生命周期，直接忽略生命周期的异常的话会导致黑屏，目前
	 * 会调用ActivityManager的finishActivity结束掉生命周期抛出异常的Activity
	 */
	private static void initActivityProtect() {
		// 各版本android的ActivityManager获取方式，finishActivity的参数，token(binder对象)的获取不一样
		if (Build.VERSION.SDK_INT >= 28) {
			iActivityProtect = new ActivityProtectV28ToV29();
		} else if (Build.VERSION.SDK_INT >= 26) {
			iActivityProtect = new ActivityProtectV26ToV27();
		} else if (Build.VERSION.SDK_INT == 25 || Build.VERSION.SDK_INT == 24) {
			iActivityProtect = new ActivityProtectV24ToV25();
		} else if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT <= 23) {
			iActivityProtect = new ActivityProtectV21ToV23();
		} else if (Build.VERSION.SDK_INT >= 15 && Build.VERSION.SDK_INT <= 20) {
			iActivityProtect = new ActivityProtectV15ToV20();
		} else if (Build.VERSION.SDK_INT < 15) {
			iActivityProtect = new ActivityProtectV15ToV20();
		}

		try {
			hookmH();
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "initActivityProtect: " + throwable.getMessage(), throwable);
		}
	}

	@SuppressLint("PrivateApi")
	private static void hookmH() throws Exception {

		final int launchActivity = 100;
		final int pauseActivity = 101;
		final int pauseActivityFinishing = 102;
		final int stopActivityHide = 104;
		final int resumeActivity = 107;
		final int destoryActivity = 109;
		final int newIntent = 112;
		final int relaunchActivity = 126;
		Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
		Object activityThread = activityThreadClass.getDeclaredMethod("currentActivityThread").invoke(null);

		Field mhField = activityThreadClass.getDeclaredField("mH");
		mhField.setAccessible(true);
		// 通过反射，hook ActivityThread内的Handler，通过此handle去处理message，如果有异常，则捕获异常，由
		// ExceptionHandler 去处理异常
		final Handler mhHandler = (Handler) mhField.get(activityThread);
		Field callbackField = Handler.class.getDeclaredField("mCallback");
		callbackField.setAccessible(true);
		callbackField.set(mhHandler, (Handler.Callback) msg -> {
			if (Build.VERSION.SDK_INT >= 28) {
				if (!isShowLog) {
					LogUtil.i(TAG, "handleMessage: >= 28");
					isShowLog = true;
				}
				final int executeTransaction = 159;
				if (msg.what == executeTransaction) {
					try {
						mhHandler.handleMessage(msg);
					} catch (Throwable throwable) {
						iActivityProtect.finishLaunchActivity(msg);
						notifyException(throwable);
					}
					return true;
				}
				return false;
			}
			if (!isShowLog) {
				LogUtil.i(TAG, "handleMessage: < 28");
				isShowLog = true;
			}
			switch (msg.what) {
				case launchActivity:
					// startActivity--> activity.attach activity.onCreate r.activity!=null activity.onStart activity.onResume
					try {
						mhHandler.handleMessage(msg);
					} catch (Throwable throwable) {
						iActivityProtect.finishLaunchActivity(msg);
						notifyException(throwable);
					}
					return true;
				case resumeActivity:
					// 回到activity onRestart onStart onResume
					try {
						mhHandler.handleMessage(msg);
					} catch (Throwable throwable) {
						iActivityProtect.finishResumeActivity(msg);
						notifyException(throwable);
					}
					return true;
				case pauseActivityFinishing:
					// 按返回键 onPause
				case pauseActivity:
					// 开启新页面时，旧页面执行 activity.onPause
					try {
						mhHandler.handleMessage(msg);
					} catch (Throwable throwable) {
						iActivityProtect.finishPauseActivity(msg);
						notifyException(throwable);
					}
					return true;
				case stopActivityHide:
					// 开启新页面时，旧页面执行 activity.onStop
					try {
						mhHandler.handleMessage(msg);
					} catch (Throwable throwable) {
						iActivityProtect.finishStopActivity(msg);
						notifyException(throwable);
					}
					return true;
				case destoryActivity:
					// 关闭activity onStop onDestroy
					try {
						mhHandler.handleMessage(msg);
					} catch (Throwable throwable) {
						notifyException(throwable);
					}
					return true;
				default:
					break;
			}
			return false;
		});
	}

	private static void notifyException(Throwable throwable) {
		if (sExceptionHandler == null) {
			return;
		}
		if (isSafeMode()) {
			sExceptionHandler.bandageExceptionHappened(throwable);
		} else {
			sExceptionHandler.uncaughtExceptionHappened(Looper.getMainLooper().getThread(), throwable);
			safeMode();
		}
	}

	private static boolean isSafeMode() {
		return sIsSafeMode;
	}

	private static void safeMode() {
		sIsSafeMode = true;
		if (sExceptionHandler != null) {
			LogUtil.i(TAG, "safeMode: enter safe mode");
		}
		while (true) {
			try {
				// 主线程的异常会从这里抛出
				Looper.loop();
			} catch (Throwable e) {
				isChoreographerException(e);
				isThreadGroupUncaughtException(e);
				if (sExceptionHandler != null) {
					sExceptionHandler.bandageExceptionHappened(e);
				}
			}
		}
	}

	/**
	 * view measure layout draw时抛出异常会导致Choreographer挂掉
	 * <p>
	 * 建议直接杀死app。以后的版本会只关闭黑屏的Activity
	 *
	 * @param e
	 */
	private static void isChoreographerException(Throwable e) {
		if (e == null || sExceptionHandler == null) {
			return;
		}
		StackTraceElement[] elements = e.getStackTrace();
		for (int i = elements.length - 1; i > -1; i--) {
			if (elements.length - i > 20) {
				return;
			}
			StackTraceElement element = elements[i];
			if ("android.view.Choreographer".equals(element.getClassName())
					&& "Choreographer.java".equals(element.getFileName())
					&& "doFrame".equals(element.getMethodName())) {
				LogUtil.e(TAG, "isChoreographerException BlackScreen , suggest killing self ");
				return;
			}
		}
	}

	private static void isThreadGroupUncaughtException(Throwable e) {
		if (e == null || sExceptionHandler == null) {
			return;
		}
		StackTraceElement[] elements = e.getStackTrace();
		for (int i = elements.length - 1; i > -1; i--) {
			if (elements.length - i > 20) {
				return;
			}
			StackTraceElement element = elements[i];
			if ("java.lang.ThreadGroup".equals(element.getClassName())) {
				LogUtil.e(TAG, "java.lang.ThreadGroup , suggest killing self ");
				return;
			}
		}
	}
}
