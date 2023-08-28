package com.yyxnb.android.intent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.TransactionTooLargeException;


import com.yyxnb.android.encrypt.utils.LogUtil;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Utilities dealing with extracting information from intents.
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public class IntentUtils {
	private static final String TAG = "IntentUtils";

	/**
	 * See {@link #isIntentTooLarge(Intent)}.
	 */
	private static final int MAX_INTENT_SIZE_THRESHOLD = 750000;

	/**
	 * Just like {@link Intent#hasExtra(String)} but doesn't throw exceptions.
	 */
	public static boolean safeHasExtra(Intent intent, String name) {
		try {
			return intent.hasExtra(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			return false;
		}
	}

	/**
	 * Just like {@link Intent#removeExtra(String)} but doesn't throw exceptions.
	 */
	public static void safeRemoveExtra(Intent intent, String name) {
		try {
			intent.removeExtra(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "removeExtra failed on intent " + t.getMessage());
		}
	}

	/**
	 * Just like {@link Bundle#getBoolean(String)} but doesn't throw exceptions.
	 *
	 * @param bundle Bundle
	 * @param name   String
	 * @return boolean default with false
	 */
	public static boolean safeGetBoolean(Bundle bundle, String name) {
		return safeGetBoolean(bundle, name, false);
	}

	/**
	 * Just like {@link Bundle#getBoolean(String, boolean)} but doesn't throw
	 * exceptions.
	 *
	 * @param bundle       Bundle
	 * @param name         String
	 * @param defaultValue long
	 * @return boolean
	 */
	public static boolean safeGetBoolean(Bundle bundle, String name, boolean defaultValue) {
		try {
			return bundle.getBoolean(name, defaultValue);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getBoolean failed with throwable: " + t.getMessage());
			return defaultValue;
		}
	}

	/**
	 * Just like {@link Intent#getBooleanExtra(String, boolean)} but doesn't throw
	 * exceptions.
	 */
	public static boolean safeGetBooleanExtra(Intent intent, String name, boolean defaultValue) {
		try {
			return intent.getBooleanExtra(name, defaultValue);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getBooleanExtra failed on intent " + t.getMessage());
			return defaultValue;
		}
	}

	/**
	 * Just like {@link Intent#getIntExtra(String, int)} but doesn't throw
	 * exceptions.
	 */
	public static int safeGetIntExtra(Intent intent, String name, int defaultValue) {
		try {
			return intent.getIntExtra(name, defaultValue);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getIntExtra failed on intent " + t.getMessage());
			return defaultValue;
		}
	}

	/**
	 * Just like {@link Bundle#getInt(String, int)} but doesn't throw exceptions.
	 */
	public static int safeGetInt(Bundle bundle, String name, int defaultValue) {
		try {
			return bundle.getInt(name, defaultValue);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getInt failed on bundle " + t.getMessage());
			return defaultValue;
		}
	}

	/**
	 * Just like {@link Intent#getIntArrayExtra(String)} but doesn't throw
	 * exceptions.
	 */
	public static int[] safeGetIntArrayExtra(Intent intent, String name) {
		try {
			return intent.getIntArrayExtra(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getIntArrayExtra failed on intent " + t.getMessage());
			return new int[0];
		}
	}

	/**
	 * Just like {@link Bundle#getIntArray(String)} but doesn't throw exceptions.
	 */
	public static int[] safeGetIntArray(Bundle bundle, String name) {
		try {
			return bundle.getIntArray(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getIntArray failed on bundle " + t.getMessage());
			return new int[0];
		}
	}

	/**
	 * Just like {@link Intent#getLongExtra(String, long)} but doesn't throw
	 * exceptions.
	 */
	public static long safeGetLongExtra(Intent intent, String name, long defaultValue) {
		try {
			return intent.getLongExtra(name, defaultValue);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getLongExtra failed on intent " + t.getMessage());
			return defaultValue;
		}
	}

	/**
	 * Just like {@link Intent#getStringExtra(String)} but doesn't throw exceptions.
	 */
	public static String safeGetStringExtra(Intent intent, String name) {
		try {
			return intent.getStringExtra(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getStringExtra failed on intent " + t.getMessage());
			return "";
		}
	}

	/**
	 * Just like {@link Bundle#getString(String)} but doesn't throw exceptions.
	 */
	public static String safeGetString(Bundle bundle, String name) {
		try {
			return bundle.getString(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getString failed on bundle " + t.getMessage());
			return "";
		}
	}

	/**
	 * Just like {@link Intent#getBundleExtra(String)} but doesn't throw exceptions.
	 */
	public static Bundle safeGetBundleExtra(Intent intent, String name) {
		try {
			return intent.getBundleExtra(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getBundleExtra failed on intent " + t.getMessage());
			return new Bundle();
		}
	}

	/**
	 * Just like {@link Bundle#getBundle(String)} but doesn't throw exceptions.
	 */
	public static Bundle safeGetBundle(Bundle bundle, String name) {
		try {
			return bundle.getBundle(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getBundle failed on bundle " + t.getMessage());
			return new Bundle();
		}
	}

	/**
	 * 此方法返回值在类型转换时还是会抛出异常，因此不建议使用，建议使用 {@link #safeGetParcelable(Bundle bundle,
	 * String key, Class)}
	 */
	@Deprecated
	public static <T extends Parcelable> T safeGetParcelable(Bundle bundle, String name) {
		try {
			return bundle.getParcelable(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getParcelable failed on bundle " + t.getMessage());
			return null;
		}
	}

	/**
	 * 此方法返回值在类型转换时还是会抛出异常，因此不建议使用，建议使用 {@link #safeGetParcelableExtra(Intent
	 * intent, String key, Class)}
	 */
	@Deprecated
	public static <T extends Parcelable> T safeGetParcelableExtra(Intent intent, String name) {
		try {
			return intent.getParcelableExtra(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getParcelableExtra failed on intent " + t.getMessage());
			return null;
		}
	}

	/**
	 * Just link {@link Intent#getParcelableArrayListExtra(String)} but doesn't
	 * throw exceptions.
	 */
	public static <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(Intent intent, String name) {
		try {
			return intent.getParcelableArrayListExtra(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getParcelableArrayListExtra failed on intent " + t.getMessage());
			return null;
		}
	}

	/**
	 * Just like {@link Intent#getParcelableArrayExtra(String)} but doesn't throw
	 * exceptions.
	 */
	public static Parcelable[] safeGetParcelableArrayExtra(Intent intent, String name) {
		try {
			return intent.getParcelableArrayExtra(name);
		} catch (Throwable t) {
			LogUtil.e(TAG, "getParcelableArrayExtra failed on intent " + t.getMessage());
			return new Parcelable[0];
		}
	}

	/**
	 * Just like {@link Intent#getStringArrayListExtra(String)} but doesn't throw
	 * exceptions.
	 */
	public static ArrayList<String> safeGetStringArrayListExtra(Intent intent, String name) {
		try {
			return intent.getStringArrayListExtra(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getStringArrayListExtra failed on intent " + t.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * Just like {@link Intent#getByteArrayExtra(String)} but doesn't throw
	 * exceptions.
	 */
	public static byte[] safeGetByteArrayExtra(Intent intent, String name) {
		try {
			return intent.getByteArrayExtra(name);
		} catch (Throwable t) {
			// Catches un-parceling exceptions.
			LogUtil.e(TAG, "getByteArrayExtra failed on intent " + t.getMessage());
			return new byte[0];
		}
	}

	/**
	 * 此方法返回值在类型转换时还是会抛出异常，因此不建议使用，建议使用 {@link #safeGetSerializableExtra(Intent
	 * intent, String key, Class)}
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static <T extends Serializable> T safeGetSerializableExtra(Intent intent, String name) {
		try {
			return (T) intent.getSerializableExtra(name);
		} catch (ClassCastException ex) {
			LogUtil.e(TAG, "Invalide class for Serializable: " + ex.getMessage());
			return null;
		} catch (Throwable t) {
			// Catches un-serializable exceptions.
			LogUtil.e(TAG, "getSerializableExtra failed on intent " + t.getMessage());
			return null;
		}
	}

	/**
	 * See {@link #safeStartActivity(Context, Intent, Bundle)}.
	 */
	public static boolean safeStartActivity(Context context, Intent intent) {
		return safeStartActivity(context, intent, null);
	}

	/**
	 * Catches any failures to start an Activity.
	 *
	 * @param context Context to use when starting the Activity.
	 * @param intent  Intent to fire.
	 * @param bundle  Bundle of launch options.
	 * @return Whether or not Android accepted the Intent.
	 */
	@SuppressLint("NewApi")
	public static boolean safeStartActivity(Context context, Intent intent, Bundle bundle) {
		try {
			context.startActivity(intent, bundle);
			return true;
		} catch (ActivityNotFoundException e) {
			// 加上堆栈打印，方便定位问题，不包含敏感信息
			LogUtil.e(TAG, "safeStartActivity: ActivityNotFoundException ", e);
		} catch (Exception e) {
			LogUtil.e(TAG, "safeStartActivityForResult: Exception");
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "safeStartActivityForResult: throwable");
		}
		return false;
	}

	/**
	 * Returns how large the Intent will be in Parcel form, which is helpful for
	 * gauging whether
	 * Android will deliver the Intent instead of throwing a
	 * TransactionTooLargeException.
	 *
	 * @param intent Intent to get the size of.
	 * @return Number of bytes required to parcel the Intent.
	 */
	public static int getParceledIntentSize(Intent intent) {
		Parcel parcel = Parcel.obtain();
		intent.writeToParcel(parcel, 0);
		int ret = parcel.dataSize();
		parcel.recycle();
		return ret;
	}

	/**
	 * Determines if an Intent's size is bigger than a reasonable threshold. Having
	 * too many large
	 * transactions in flight simultaneously (including Intents) causes Android to
	 * throw a
	 * {@link TransactionTooLargeException}. According to that class, the limit
	 * across all
	 * transactions combined is one megabyte. Best practice is to keep each
	 * individual Intent well
	 * under the limit to avoid this situation.
	 */
	public static boolean isIntentTooLarge(Intent intent) {
		return getParceledIntentSize(intent) > MAX_INTENT_SIZE_THRESHOLD;
	}

	@Deprecated
	public boolean safeStartActivityForResult(Activity activity, Intent intent, int requestCode) {
		try {
			activity.startActivityForResult(intent, requestCode);
			return true;
		} catch (ActivityNotFoundException e) {
			LogUtil.e(TAG, "safeStartActivityForResult: ActivityNotFoundException ", e);
		} catch (Exception e) {
			LogUtil.e(TAG, "safeStartActivityForResult: Exception ", e);
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "safeStartActivityForResult: throwable ", throwable);
		}
		return false;
	}

	public static boolean safeStartActivityForResultStatic(Activity activity, Intent intent, int requestCode) {
		try {
			activity.startActivityForResult(intent, requestCode);
			return true;
		} catch (ActivityNotFoundException e) {
			LogUtil.e(TAG, "safeStartActivityForResult: ActivityNotFoundException ", e);
		} catch (Exception e) {
			LogUtil.e(TAG, "safeStartActivityForResult: Exception ", e);
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "safeStartActivityForResult: throwable ", throwable);
		}
		return false;
	}

	@SuppressLint("NewApi")
	@Deprecated
	public boolean safeStartActivityForResult(Activity activity, Intent intent, int requestCode, Bundle options) {
		try {
			activity.startActivityForResult(intent, requestCode, options);
			return true;
		} catch (ActivityNotFoundException e) {
			LogUtil.e(TAG, "safeStartActivityForResult: ActivityNotFoundException ", e);
		} catch (Exception e) {
			LogUtil.e(TAG, "safeStartActivityForResult: Exception");
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "safeStartActivityForResult: throwable");
		}
		return false;
	}

	@SuppressLint("NewApi")
	public static boolean safeStartActivityForResultStatic(Activity activity, Intent intent, int requestCode,
														   Bundle options) {
		try {
			activity.startActivityForResult(intent, requestCode, options);
			return true;
		} catch (ActivityNotFoundException e) {
			LogUtil.e(TAG, "safeStartActivityForResult: ActivityNotFoundException ", e);
		} catch (Exception e) {
			LogUtil.e(TAG, "safeStartActivityForResult: Exception");
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "safeStartActivityForResult: throwable");
		}
		return false;
	}

	@SuppressLint("NewApi")
	public static Uri getReferrer(Activity activity) {
		try {
			return activity.getReferrer();
		} catch (Throwable e) {
			LogUtil.e(TAG, "getReferrer: " + e.getMessage());
			return null;
		}
	}

	/**
	 * getXXXExtra时，系统会进行反序列化操作，会调用BaseBundle类中的unparcel方法，如果发现无法反序列化传输过来的intent中的Parcelable对象，就会抛出类似
	 * android.os.BadParcelableException: ClassNotFoundException when unmarshalling:
	 * com.cc.test11.MyParcelable 异常。此方法可用于检测Intent序列化攻击。
	 * 如果检测到，直接finish Activity，也可防御DOS攻击。此时可不必使用SafeIntent。
	 *
	 * @return boolean
	 */
	public static boolean hasIntentBomb(Intent intent) {
		boolean hasIntentBomb = false;
		if (intent == null) {
			LogUtil.e(TAG, "intent is null");
			hasIntentBomb = true;
		} else {
			if (intent instanceof SafeIntent) {
				LogUtil.i(TAG, "safe intent");
				hasIntentBomb = ((SafeIntent) intent).hasIntentBomb();
			} else {
				try {
					intent.getStringExtra("ANYTHING");
				} catch (Throwable e) {
					hasIntentBomb = true;
				}
			}
		}
		if (hasIntentBomb) {
			LogUtil.e(TAG, "hasIntentBomb");
		}
		return hasIntentBomb;
	}

	/**
	 * 从bundle中获取序列化数据，并将其转换成给定的类，如果不属于给定的类，则返回null
	 * 可以防止两类异常：1、反序列化异常 2、类型转换异常
	 *
	 * @param bundle bundle
	 * @param key    key
	 * @param tClass tClass
	 * @param <T>    T
	 * @return Parcelable
	 */
	public static <T extends Parcelable> T safeGetParcelable(Bundle bundle, String key, Class<T> tClass) {
		try {
			Parcelable parcelable = bundle.getParcelable(key);
			if (tClass.isInstance(parcelable)) {
				return tClass.cast(parcelable);
			}
		} catch (Throwable e) {
			LogUtil.e(TAG, "getParcelable exception: " + e.getMessage());
		}
		return null;
	}

	/**
	 * 从bundle中获取序列化数据，并将其转换成给定的类，如果不属于给定的类，则返回null
	 * 可以防止两类异常：1、反序列化异常 2、类型转换异常
	 *
	 * @param bundle bundle
	 * @param key    key
	 * @param tClass tClass
	 * @param <T>    T
	 * @return T
	 */
	public static <T extends Serializable> T safeGetSerializable(Bundle bundle, String key, Class<T> tClass) {
		try {
			Serializable serializable = bundle.getSerializable(key);
			if (tClass.isInstance(serializable)) {
				return tClass.cast(serializable);
			}
		} catch (Throwable e) {
			LogUtil.e(TAG, "getSerializable exception: " + e.getMessage());
		}
		return null;
	}

	/**
	 * 从 intent 中获取序列化数据，并将其转换成给定的类，如果不属于给定的类，则返回null
	 * 可以防止两类异常：1、反序列化异常 2、类型转换异常
	 *
	 * @param intent intent
	 * @param key    key
	 * @param tClass tClass
	 * @param <T>    T
	 * @return T
	 */
	public static <T extends Parcelable> T safeGetParcelableExtra(Intent intent, String key, Class<T> tClass) {
		try {
			Parcelable parcelable = intent.getParcelableExtra(key);
			if (tClass.isInstance(parcelable)) {
				return tClass.cast(parcelable);
			}
		} catch (Throwable e) {
			LogUtil.e(TAG, "getParcelable exception: " + e.getMessage());
		}
		return null;
	}

	/**
	 * 从 intent 中获取序列化数据，并将其转换成给定的类，如果不属于给定的类，则返回null
	 * 可以防止两类异常：1、反序列化异常 2、类型转换异常
	 *
	 * @param intent intent
	 * @param key    key
	 * @param tClass tClass
	 * @param <T>    T
	 * @return T
	 */
	public static <T extends Serializable> T safeGetSerializableExtra(Intent intent, String key, Class<T> tClass) {
		try {
			Serializable serializable = intent.getSerializableExtra(key);
			if (tClass.isInstance(serializable)) {
				return tClass.cast(serializable);
			}
		} catch (Throwable e) {
			LogUtil.e(TAG, "getSerializable exception: " + e.getMessage());
		}
		return null;
	}

	/**
	 * 过滤intent
	 *
	 * @param intent intent
	 */
	public static void filterIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		intent.addCategory("android.intent.category.BROWSABLE").setComponent(null);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			intent.setSelector(null);
		}

		// 通过设置clipdata，防止系统在处理特殊action的时候自动添加读flag，详情可参考Intent类中的migrateExtraStreamToClipData方法
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			if (intent.getClipData() == null) {
				intent.setClipData(ClipData.newPlainText("avoid intent migrateExtraStreamToClipData add flags", "anything"));
			}
		}

		// 删除读写权限
		int flag = intent.getFlags();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			flag &= ~Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			flag &= ~Intent.FLAG_GRANT_PREFIX_URI_PERMISSION;
		}
		flag &= ~Intent.FLAG_GRANT_READ_URI_PERMISSION;
		flag &= ~Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
		intent.setFlags(flag);
	}

	/**
	 * 安全转换uri为intent。
	 *
	 * @param uri uri
	 * @return Intent
	 * @throws URISyntaxException URISyntaxException
	 */
	public static Intent safeParseUri(String uri, int flag) throws URISyntaxException {
		if (uri == null) {
			return null;
		}
		Intent intent = Intent.parseUri(uri, flag);
		filterIntent(intent);
		return intent;
	}

	/**
	 * 获取调用APP的包名
	 *
	 * @param activity 当前 Activity
	 * @return 调用APP的包名
	 */
	public static String getCallerPackage(Activity activity) {
		LogUtil.i(TAG, "getCallerPackage");
		if (activity == null) {
			LogUtil.e(TAG, "getCallerPackage, activity is null");
			return "";
		}

		try {
			Object manager;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				manager = ActivityManager.class.getMethod("getService").invoke(null);
			} else {
				manager = Class.forName("android.app.ActivityManagerNative").getMethod("getDefault").invoke(null);
			}

			IBinder binder = (IBinder) Activity.class.getMethod("getActivityToken").invoke(activity);
			int uid = (int) manager.getClass().getMethod("getLaunchedFromUid", IBinder.class).invoke(manager, binder);

			String[] packages = activity.getPackageManager().getPackagesForUid(uid);
			if (null == packages || packages.length < 1) {
				LogUtil.e(TAG, "getCallerPackage, packages is null or empty");
				return "";
			}
			String packageName = packages[0];
			LogUtil.i(TAG, "getCallerPackage, package is: " + packageName);
			return packageName;
		} catch (Exception e) {
			LogUtil.e(TAG, "getCallerPackage exception: " + e.getMessage());
		}
		return "";
	}
}
