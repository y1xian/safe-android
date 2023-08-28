package com.yyxnb.android.intent;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.yyxnb.android.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

/**
 * 用于从Intent中获取Bundle后进行二次封装, 对Bundle的所有接口进行异常保护
 * <p>
 * <pre>
 *     final Intent intent = new SafeIntent(getIntent());
 *     final SafeBundle bundle = new SafeBundle(intent.getExtras());
 *     final Parcelable parcelable = bundle.getParcelable(SIGN_IN_INTENT_KEY);
 * </pre>
 * <p>
 * 考虑到本类和Bundle类型不一致, 仅获取个别字段或者少量字段的场景,
 * 建议使用{@link SafeIntent}或者{@link IntentUtils}
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public class SafeBundle {

	private static final String TAG = "SafeBundle";

	private static final String EMPTY = "";
	private final Bundle bundle;

	public SafeBundle() {
		this(new Bundle());
	}

	public SafeBundle(Bundle bundle) {
		this.bundle = bundle != null ? bundle : new Bundle();
	}

	public Bundle getBundle() {
		return bundle;
	}

	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		try {
			return bundle.getBoolean(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getBoolean exception : " + e.getMessage());
			return defaultValue;
		}
	}

	public boolean[] getBooleanArray(String key) {
		try {
			return bundle.getBooleanArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getBooleanArray exception: " + e.getMessage());
			return new boolean[0];
		}
	}

	public boolean[] getBooleanArrayReturnNotNull(String key) {
		boolean[] result;
		try {
			result = bundle.getBooleanArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getBooleanArray exception: " + e.getMessage());
			return new boolean[0];
		}
		if (result == null) {
			return new boolean[0];
		}
		return result;
	}

	public byte getByte(String key) {
		try {
			return bundle.getByte(key);
		} catch (Throwable e) {
			return (byte) 0;
		}
	}

	public byte getByte(String key, byte defaultValue) {
		try {
			return bundle.getByte(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getByte exception: " + e.getMessage());
			return defaultValue;
		}
	}

	public byte[] getByteArray(String key) {
		try {
			return bundle.getByteArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getByteArray exception: " + e.getMessage());
			return new byte[0];
		}
	}

	public byte[] getByteArrayReturnNotNull(String key) {
		byte[] result;
		try {
			result = bundle.getByteArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getByteArray exception: " + e.getMessage());
			return new byte[0];
		}
		if (result == null) {
			return new byte[0];
		}
		return result;
	}

	public char getChar(String key) {
		try {
			return bundle.getChar(key);
		} catch (Throwable e) {
			return (char) 0;
		}
	}

	public char getChar(String key, char defaultValue) {
		try {
			return bundle.getChar(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getChar exception: " + e.getMessage());
			return defaultValue;
		}
	}

	public char[] getCharArray(String key) {
		try {
			return bundle.getCharArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getCharArray exception: " + e.getMessage());
			return new char[0];
		}
	}

	public char[] getCharArrayReturnNotNull(String key) {
		char[] result;
		try {
			result = bundle.getCharArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getCharArray exception: " + e.getMessage());
			return new char[0];
		}
		if (result == null) {
			return new char[0];
		}
		return result;
	}

	public short getShort(String key) {
		try {
			return bundle.getShort(key);
		} catch (Throwable e) {
			return (short) 0;
		}
	}

	public short getShort(String key, short defaultValue) {
		try {
			return bundle.getShort(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getShort exception: " + e.getMessage());
			return defaultValue;
		}
	}

	public short[] getShortArray(String key) {
		try {
			return bundle.getShortArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getShortArray exception: " + e.getMessage());
			return new short[0];
		}
	}

	public short[] getShortArrayReturnNotNull(String key) {
		short[] result;
		try {
			result = bundle.getShortArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getShortArray exception: " + e.getMessage());
			return new short[0];
		}
		if (result == null) {
			return new short[0];
		}
		return result;
	}


	public CharSequence getCharSequence(String key) {
		try {
			return bundle.getCharSequence(key);
		} catch (Throwable e) {
			return "";
		}
	}

	public CharSequence getCharSequenceReturnNotNull(String key) {
		CharSequence charSequence = "";
		try {
			charSequence = bundle.getCharSequence(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getCharSequenceReturnNotNull exception: " + e.getMessage());
		}
		if (TextUtils.isEmpty(charSequence)) {
			return EMPTY;
		}
		return charSequence;
	}

	@SuppressLint("NewApi")
	public CharSequence getCharSequence(String key, CharSequence defaultValue) {
		try {
			return bundle.getCharSequence(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getCharSequence exception: " + e.getMessage());
			return defaultValue;
		}
	}

	@SuppressLint("NewApi")
	public CharSequence getCharSequenceReturnNotNull(String key, CharSequence defaultValue) {
		CharSequence result;
		try {
			result = bundle.getCharSequence(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getCharSequence exception: " + e.getMessage());
			return defaultValue;
		}
		if (result == null) {
			return EMPTY;
		}
		return result;
	}

	public CharSequence[] getCharSequenceArray(String key) {
		try {
			return bundle.getCharSequenceArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getCharSequenceArray exception: " + e.getMessage());
			return new CharSequence[0];
		}
	}

	public CharSequence[] getCharSequenceArrayReturnNotNull(String key) {
		CharSequence[] result;
		try {
			result = bundle.getCharSequenceArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getCharSequenceArrayReturnNotNull exception: " + e.getMessage());
			return new CharSequence[0];
		}
		if (result == null) {
			return new CharSequence[0];
		}
		return result;
	}

	public ArrayList<CharSequence> getCharSequenceArrayList(String key) {
		try {
			return bundle.getCharSequenceArrayList(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getCharSequenceArrayList exception: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public ArrayList<CharSequence> getCharSequenceArrayListReturnNotNull(String key) {
		ArrayList<CharSequence> result;
		try {
			result = bundle.getCharSequenceArrayList(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getCharSequenceArrayList exception: " + e.getMessage());
			return new ArrayList<>();
		}
		if (result == null) {
			return new ArrayList<>();
		}
		return result;
	}

	public int getInt(String key) {
		return getInt(key, 0);
	}

	public int getInt(String key, int defaultValue) {
		try {
			return bundle.getInt(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getInt exception: " + e.getMessage());
			return defaultValue;
		}
	}

	public int[] getIntArray(String key) {
		try {
			return bundle.getIntArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getIntArray exception: " + e.getMessage());
			return new int[0];
		}
	}

	public int[] getIntArrayReturnNotNull(String key) {
		int[] result;
		try {
			result = bundle.getIntArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getIntArray exception: " + e.getMessage());
			return new int[0];
		}
		if (result == null) {
			return new int[0];
		}
		return result;
	}

	public ArrayList<Integer> getIntegerArrayList(String key) {
		try {
			return bundle.getIntegerArrayList(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getIntegerArrayList exception: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public ArrayList<Integer> getIntegerArrayListReturnNotNull(String key) {
		ArrayList<Integer> result;
		try {
			result = bundle.getIntegerArrayList(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getIntegerArrayList exception: " + e.getMessage());
			return new ArrayList<>();
		}
		if (result == null) {
			return new ArrayList<>();
		}
		return result;
	}

	public float getFloat(String key) {
		return getFloat(key, 0.0f);
	}

	public float getFloat(String key, float defaultValue) {
		try {
			return bundle.getFloat(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getFloat exception: " + e.getMessage());
			return defaultValue;
		}
	}

	public float[] getFloatArray(String key) {
		try {
			return bundle.getFloatArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getFloatArray exception: " + e.getMessage());
			return new float[0];
		}
	}

	public float[] getFloatArrayReturnNotNull(String key) {
		float[] result;
		try {
			result = bundle.getFloatArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getFloatArray exception: " + e.getMessage());
			return new float[0];
		}
		if (result == null) {
			return new float[0];
		}
		return result;
	}

	public long getLong(String key) {
		return getLong(key, 0L);
	}

	public long getLong(String key, long defaultValue) {
		try {
			return bundle.getLong(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getLong exception: " + e.getMessage());
			return defaultValue;
		}
	}

	public long[] getLongArray(String key) {
		try {
			return bundle.getLongArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getLongArray exception: " + e.getMessage());
			return new long[0];
		}
	}

	public long[] getLongArrayReturnNotNull(String key) {
		long[] result;
		try {
			result = bundle.getLongArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getLongArray exception: " + e.getMessage());
			return new long[0];
		}
		if (result == null) {
			return new long[0];
		}
		return result;
	}

	public double getDouble(String key) {
		return getDouble(key, 0.0);
	}

	public double getDouble(String key, double defaultValue) {
		try {
			return bundle.getDouble(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getDouble exception: " + e.getMessage());
			return defaultValue;
		}
	}

	public double[] getDoubleArray(String key) {
		try {
			return bundle.getDoubleArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getDoubleArray exception: " + e.getMessage());
			return new double[0];
		}
	}

	public double[] getDoubleArrayReturnNotNull(String key) {
		double[] result;
		try {
			result = bundle.getDoubleArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getDoubleArray exception: " + e.getMessage());
			return new double[0];
		}
		if (result == null) {
			return new double[0];
		}
		return result;
	}

	public String getString(String key) {
		try {
			return bundle.getString(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getString exception: " + e.getMessage());
			return "";
		}
	}

	public String getStringReturnNotNull(String key) {
		String result = "";
		try {
			result = bundle.getString(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getString exception: " + e.getMessage());
		}
		if (TextUtils.isEmpty(result)) {
			return EMPTY;
		}
		return result;
	}


	@SuppressLint("NewApi")
	public String getString(String key, String defaultValue) {
		try {
			return bundle.getString(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getString exception: " + e.getMessage());
			return defaultValue;
		}
	}


	@SuppressLint("NewApi")
	public String getStringReturnNotNull(String key, String defaultValue) {
		String result;
		try {
			result = bundle.getString(key, defaultValue);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getString exception: " + e.getMessage());
			return defaultValue;
		}
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	public String[] getStringArray(String key) {
		try {
			return bundle.getStringArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getStringArray exception: " + e.getMessage());
			return new String[0];
		}
	}

	public String[] getStringArrayReturnNotNull(String key) {
		String[] result;
		try {
			result = bundle.getStringArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getStringArray exception: " + e.getMessage());
			return new String[0];
		}
		if (result == null) {
			return new String[0];
		}
		return result;
	}

	public ArrayList<String> getStringArrayList(String key) {
		try {
			return bundle.getStringArrayList(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getStringArrayList exception: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	public ArrayList<String> getStringArrayListReturnNotNull(String key) {
		ArrayList<String> result;
		try {
			result = bundle.getStringArrayList(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getStringArrayList exception: " + e.getMessage());
			return new ArrayList<>();
		}
		if (result == null) {
			return new ArrayList<>();
		}
		return result;
	}

	public Size getSize(String key) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				return bundle.getSize(key);
			} else {
				return null;
			}
		} catch (Throwable e) {
			LogUtil.e(TAG, "getSize exception: " + e.getMessage());
			return null;
		}
	}

	public SizeF getSizeF(String key) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				return bundle.getSizeF(key);
			} else {
				return null;
			}
		} catch (Throwable e) {
			LogUtil.e(TAG, "getSizeF exception: " + e.getMessage());
			return null;
		}
	}

	public <T extends Parcelable> T getParcelable(String key) {
		try {
			return bundle.getParcelable(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getParcelable exception: " + e.getMessage());
			return null;
		}
	}

	public <T extends Parcelable> T getParcelable(String key, Class<T> tClass) {
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

	public Parcelable[] getParcelableArray(String key) {
		try {
			return bundle.getParcelableArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getParcelableArray exception: " + e.getMessage());
			return new Parcelable[0];
		}
	}

	public Parcelable[] getParcelableArrayReturnNotNull(String key) {
		Parcelable[] result;
		try {
			result = bundle.getParcelableArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getParcelableArray exception: " + e.getMessage());
			return new Parcelable[0];
		}
		if (result == null) {
			return new Parcelable[0];
		}
		return result;
	}

	public <T extends Parcelable> ArrayList<T> getParcelableArrayList(String key) {
		try {
			return bundle.getParcelableArrayList(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getParcelableArrayList exception: " + e.getMessage());
			return null;
		}
	}

	public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(String key) {
		try {
			return bundle.getSparseParcelableArray(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getSparseParcelableArray exception: " + e.getMessage());
			return null;
		}
	}

	public Serializable getSerializable(String key) {
		try {
			return bundle.getSerializable(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getSerializable exception: " + e.getMessage());
			return null;
		}
	}

	public <T extends Serializable> T getSerializable(String key, Class<T> tClass) {
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

	@SuppressLint("NewApi")
	public IBinder getBinder(@Nullable String key) {
		try {
			return bundle.getBinder(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getBinder exception: " + e.getMessage());
			return null;
		}
	}

	public Bundle getBundle(@Nullable String key) {
		try {
			return bundle.getBundle(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getBundle exception: " + e.getMessage());
			return null;
		}
	}

	public Bundle getBundleReturnNotNull(@Nullable String key) {
		Bundle result;
		try {
			result = bundle.getBundle(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "getBundle exception: " + e.getMessage());
			return new Bundle();
		}
		if (result == null) {
			return new Bundle();
		}
		return result;
	}

	public Object get(String key) {
		try {
			return bundle.get(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "get exception: " + e.getMessage());
			return null;
		}
	}

	public Object getReturnNotNull(String key) {
		Object result;
		try {
			result = bundle.get(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "get exception: " + e.getMessage());
			return new Object();
		}
		if (result == null) {
			return new Object();
		}
		return result;
	}

	public SafeBundle putBoolean(@Nullable String key, boolean value) {
		try {
			bundle.putBoolean(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putBoolean exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putInt(@Nullable String key, int value) {
		try {
			bundle.putInt(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putInt exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putLong(@Nullable String key, long value) {
		try {
			bundle.putLong(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putLong exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putDouble(@Nullable String key, double value) {
		try {
			bundle.putDouble(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putDouble exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putString(@Nullable String key, @Nullable String value) {
		try {
			bundle.putString(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putString exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putBooleanArray(@Nullable String key, @Nullable boolean[] value) {
		try {
			bundle.putBooleanArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putBooleanArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putIntArray(@Nullable String key, @Nullable int[] value) {
		try {
			bundle.putIntArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putIntArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putLongArray(@Nullable String key, @Nullable long[] value) {
		try {
			bundle.putLongArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putLongArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putDoubleArray(@Nullable String key, @Nullable double[] value) {
		try {
			bundle.putDoubleArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putDoubleArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putStringArray(@Nullable String key, @Nullable String[] value) {
		try {
			bundle.putStringArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putStringArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putAll(Bundle bundle) {
		try {
			if (bundle != null) {
				this.bundle.putAll(bundle);
			}
		} catch (Throwable e) {
			LogUtil.e(TAG, "putAll exception");
		}
		return this;
	}

	public SafeBundle putByte(@Nullable String key, byte value) {
		try {
			bundle.putByte(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putByte exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putChar(@Nullable String key, char value) {
		try {
			bundle.putChar(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putChar exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putShort(@Nullable String key, short value) {
		try {
			bundle.putShort(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putShort exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putFloat(@Nullable String key, float value) {
		try {
			bundle.putFloat(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putFloat exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putCharSequence(@Nullable String key, @Nullable CharSequence value) {
		try {
			bundle.putCharSequence(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putCharSequence exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putParcelable(@Nullable String key, @Nullable Parcelable value) {
		try {
			bundle.putParcelable(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putParcelable exception: " + e.getMessage());
		}
		return this;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SafeBundle putSize(@Nullable String key, @Nullable Size value) {
		try {
			bundle.putSize(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putSize exception: " + e.getMessage());
		}
		return this;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SafeBundle putSizeF(@Nullable String key, @Nullable SizeF value) {
		try {
			bundle.putSizeF(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putSizeF exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
		try {
			bundle.putParcelableArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putParcelableArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
		try {
			bundle.putParcelableArrayList(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putParcelableArrayList exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
		try {
			bundle.putSparseParcelableArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putSparseParcelableArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
		try {
			bundle.putIntegerArrayList(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putIntegerArrayList exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
		try {
			bundle.putStringArrayList(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putStringArrayList exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
		try {
			bundle.putCharSequenceArrayList(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putCharSequenceArrayList exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putSerializable(@Nullable String key, @Nullable Serializable value) {
		try {
			bundle.putSerializable(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putSerializable exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putByteArray(@Nullable String key, @Nullable byte[] value) {
		try {
			bundle.putByteArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putByteArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putShortArray(@Nullable String key, @Nullable short[] value) {
		try {
			bundle.putShortArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putShortArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putCharArray(@Nullable String key, @Nullable char[] value) {
		try {
			bundle.putCharArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putCharArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putFloatArray(@Nullable String key, @Nullable float[] value) {
		try {
			bundle.putFloatArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putFloatArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
		try {
			bundle.putCharSequenceArray(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putCharSequenceArray exception: " + e.getMessage());
		}
		return this;
	}

	public SafeBundle putBundle(@Nullable String key, @Nullable Bundle value) {
		try {
			bundle.putBundle(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putBundle exception: " + e.getMessage());
		}
		return this;
	}


	@SuppressLint("NewApi")
	public SafeBundle putBinder(@Nullable String key, @Nullable IBinder value) {
		try {
			bundle.putBinder(key, value);
		} catch (Throwable e) {
			LogUtil.e(TAG, "putBundle exception: " + e.getMessage());
		}
		return this;
	}

	public int size() {
		try {
			return bundle.size();
		} catch (Throwable e) {
			LogUtil.e(TAG, "size exception");
			return 0;
		}
	}

	public boolean isEmpty() {
		try {
			return bundle.isEmpty();
		} catch (Throwable e) {
			LogUtil.e(TAG, "isEmpty exception");
			return true;
		}
	}

	/**
	 * Removes all elements from the mapping of this Bundle.
	 */
	public void clear() {
		try {
			bundle.clear();
		} catch (Throwable e) {
			LogUtil.e(TAG, "clear exception.");
		}
	}

	/**
	 * Returns true if the given key is contained in the mapping
	 * of this Bundle.
	 *
	 * @param key a String key
	 * @return true if the key is part of the mapping, false otherwise
	 */
	public boolean containsKey(String key) {
		try {
			return bundle.containsKey(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "containsKey exception. key:");
			return false;
		}
	}

	/**
	 * Removes any entry with the given key from the mapping of this Bundle.
	 *
	 * @param key a String key
	 */
	public void remove(String key) {
		try {
			bundle.remove(key);
		} catch (Throwable e) {
			LogUtil.e(TAG, "remove exception. key:");
		}
	}

	/**
	 * Returns a Set containing the Strings used as keys in this Bundle.
	 *
	 * @return a Set of String keys
	 */
	public Set<String> keySet() {
		try {
			return bundle.keySet();
		} catch (Throwable e) {
			LogUtil.e(TAG, "keySet exception.");
			return null;
		}
	}


	@Override
	public String toString() {
		try {
			return bundle.toString();
		} catch (Throwable e) {
			LogUtil.e(TAG, "toString exception.");
			return null;
		}

	}
}
