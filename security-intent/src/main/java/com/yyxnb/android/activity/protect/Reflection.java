package com.yyxnb.android.activity.protect;

import static android.os.Build.VERSION.SDK_INT;

import android.content.Context;
import android.os.Build;

import com.yyxnb.android.encrypt.utils.LogUtil;

import java.lang.reflect.Method;

/**
 * Reflection
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public class Reflection {
	private static final String TAG = "Reflection";

	private static Object sVmRuntime;
	private static Method setHiddenApiExemptions;
	private static final int ERROR_EXEMPT_FAILED = -21;

	static {
		if (SDK_INT >= Build.VERSION_CODES.P) {
			try {
				Method forName = Class.class.getDeclaredMethod("forName", String.class);
				Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
				Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
				Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
				setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
				sVmRuntime = getRuntime.invoke(null);
			} catch (Throwable e) {
				LogUtil.e(TAG, "reflect failed : " + e.getMessage());
			}
		}
	}


	public static int unseal(Context context) {
		if (SDK_INT < 28) {
			// Below Android P, ignore
			return 0;
		}

		// try exempt API first.
		if (exemptAll()) {
			return 0;
		} else {
			return ERROR_EXEMPT_FAILED;
		}
	}

	/**
	 * make the method exempted from hidden API check.
	 *
	 * @param method the method signature prefix.
	 * @return true if success.
	 */
	public static boolean exempt(String method) {
		return exempt(new String[]{method});
	}

	/**
	 * make specific methods exempted from hidden API check.
	 *
	 * @param methods the method signature prefix, such as "Ldalvik/system", "Landroid" or even "L"
	 * @return true if success
	 */
	public static boolean exempt(String... methods) {
		if (sVmRuntime == null || setHiddenApiExemptions == null) {
			return false;
		}

		try {
			setHiddenApiExemptions.invoke(sVmRuntime, new Object[]{methods});
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	/**
	 * Make all hidden API exempted.
	 *
	 * @return true if success.
	 */
	public static boolean exemptAll() {
		return exempt(new String[]{"L"});
	}
}
