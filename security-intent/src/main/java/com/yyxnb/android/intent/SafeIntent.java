package com.yyxnb.android.intent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.yyxnb.android.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * [获取序列化数据时增加保护的Intent]<BR>
 * [推荐使用方法： 1. 覆写Activity getIntent方法，对super结果判空后，返回new SafeIntent(intent) 2. 对外的广播替换为SafeBroadcastReceiver 3.
 * 对外的Service，在onStartCommand里，对intent判空后，封装为SafeIntent]<BR>
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public class SafeIntent extends Intent {
	private static final String EMPTY = "";

	private static final String TAG = SafeIntent.class.getSimpleName();

	/**
	 * [构造简要说明]
	 *
	 * @param intent 原始intent
	 */
	public SafeIntent(Intent intent) {
		super(intent == null ? new Intent() : intent);
	}


	@Override
	public String getAction() {
		try {
			return super.getAction();
		} catch (Throwable e) {
			return "";
		}
	}

	public String getActionReturnNotNull() {
		String result;
		try {
			result = super.getAction();
		} catch (Throwable e) {
			return EMPTY;
		}
		if (result == null) {
			return EMPTY;
		}
		return result;
	}

	@Override
	public Intent setAction(@Nullable String action) {
		try {
			return super.setAction(action);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public Intent setPackage(@Nullable String packageName) {
		try {
			return super.setPackage(packageName);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public void setSelector(@Nullable Intent selector) {
		try {
			super.setSelector(selector);
		} catch (Throwable e) {
			LogUtil.e(TAG, "setSelector: " + e.getMessage());
		}
	}

	@Override
	public String toUri(int flags) {
		try {
			return super.toUri(flags);
		} catch (Throwable e) {
			LogUtil.e(TAG, "toUri: " + e.getMessage());
			return "";
		}
	}

	public String toUriReturnNotNull(int flags) {
		String result = EMPTY;
		try {
			result = super.toUri(flags);
		} catch (Throwable e) {
			LogUtil.e(TAG, "toUri: " + e.getMessage());
		}
		if (result == null) {
			return EMPTY;
		}
		return result;
	}

	@Override
	public <T extends Parcelable> T getParcelableExtra(String name) {
		try {
			return super.getParcelableExtra(name);
		} catch (Throwable e) {
			return null;
		}
	}


	@Override
	public Intent putExtra(String name, Parcelable value) {
		try {
			return super.putExtra(name, value);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public Intent putExtra(String name, Parcelable[] value) {
		try {
			return super.putExtra(name, value);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public Intent putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
		try {
			return super.putParcelableArrayListExtra(name, value);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public Intent putExtra(String name, Serializable value) {
		try {
			return super.putExtra(name, value);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public void removeExtra(String name) {
		try {
			super.removeExtra(name);
		} catch (Throwable e) {
			LogUtil.e(TAG, "removeExtra: " + e.getMessage());
		}
	}

	@Override
	public String getStringExtra(String name) {
		try {
			return super.getStringExtra(name);
		} catch (Throwable e) {
			return "";
		}
	}

	public String getStringExtraReturnNotNull(String name) {
		String result;
		try {
			result = super.getStringExtra(name);
		} catch (Throwable e) {
			return EMPTY;
		}
		if (result == null) {
			return EMPTY;
		}
		return result;
	}

	@Override
	public Intent putExtra(String name, int value) {
		try {
			return super.putExtra(name, value);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public Intent putExtra(String name, String value) {
		try {
			return super.putExtra(name, value);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public Intent putExtras(Intent src) {
		try {
			return super.putExtras(src);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public Intent putExtra(String name, String[] value) {
		try {
			return super.putExtra(name, value);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public Intent putExtra(String name, CharSequence value) {
		try {
			return super.putExtra(name, value);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public Intent putExtra(String name, CharSequence[] value) {
		try {
			return super.putExtra(name, value);
		} catch (Throwable e) {
			return this;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getBooleanExtra(String name, boolean defaultValue) {
		try {
			return super.getBooleanExtra(name, defaultValue);
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIntExtra(String name, int defaultValue) {
		try {
			return super.getIntExtra(name, defaultValue);
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte getByteExtra(String name, byte defaultValue) {
		try {
			return super.getByteExtra(name, defaultValue);
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	@Override
	public CharSequence getCharSequenceExtra(String name) {
		try {
			return super.getCharSequenceExtra(name);
		} catch (Throwable e) {
			return "";
		}
	}

	public CharSequence getCharSequenceExtraReturnNotNull(String name) {
		CharSequence result;
		try {
			result = super.getCharSequenceExtra(name);
		} catch (Throwable e) {
			return EMPTY;
		}
		if (result == null) {
			return EMPTY;
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public char getCharExtra(String name, char defaultValue) {
		try {
			return super.getCharExtra(name, defaultValue);
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLongExtra(String name, long defaultValue) {
		try {
			return super.getLongExtra(name, defaultValue);
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getFloatExtra(String name, float defaultValue) {
		try {
			return super.getFloatExtra(name, defaultValue);
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDoubleExtra(String name, double defaultValue) {
		try {
			return super.getDoubleExtra(name, defaultValue);
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public Serializable getSerializableExtra(String name) {
		try {
			return super.getSerializableExtra(name);
		} catch (Throwable e) {
			return null;
		}
	}

	public <T extends Serializable> T getSerializableExtra(String key, Class<T> tClass) {
		try {
			Serializable serializable = super.getSerializableExtra(key);
			if (tClass.isInstance(serializable)) {
				return tClass.cast(serializable);
			}
		} catch (Throwable e) {
			LogUtil.e(TAG, "getSerializable exception: " + e.getMessage());
		}
		return null;
	}

	@Override
	public ArrayList<CharSequence> getCharSequenceArrayListExtra(String name) {
		try {
			return super.getCharSequenceArrayListExtra(name);
		} catch (Throwable e) {
			return new ArrayList<>();
		}
	}

	public ArrayList<CharSequence> getCharSequenceArrayListExtraReturnNotNull(String name) {
		ArrayList<CharSequence> result;
		try {
			result = super.getCharSequenceArrayListExtra(name);
		} catch (Throwable e) {
			return new ArrayList<>();
		}
		if (result == null) {
			return new ArrayList<>();
		}
		return result;
	}

	@Override
	public ArrayList<Integer> getIntegerArrayListExtra(String name) {
		try {
			return super.getIntegerArrayListExtra(name);
		} catch (Throwable e) {
			return new ArrayList<>();
		}
	}

	public ArrayList<Integer> getIntegerArrayListExtraReturnNotNull(String name) {
		ArrayList<Integer> result;
		try {
			result = super.getIntegerArrayListExtra(name);
		} catch (Throwable e) {
			return new ArrayList<>();
		}
		if (result == null) {
			return new ArrayList<>();
		}
		return result;
	}

	@Override
	public ArrayList<String> getStringArrayListExtra(String name) {
		try {
			return super.getStringArrayListExtra(name);
		} catch (Throwable e) {
			return new ArrayList<>();
		}
	}

	public ArrayList<String> getStringArrayListExtraReturnNotNull(String name) {
		ArrayList<String> result;
		try {
			result = super.getStringArrayListExtra(name);
		} catch (Throwable e) {
			return new ArrayList<>();
		}
		if (result == null) {
			return new ArrayList<>();
		}
		return result;
	}

	@Override
	public <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(String name) {
		try {
			return super.getParcelableArrayListExtra(name);
		} catch (Throwable e) {
			return null;
		}
	}

	@Override
	public boolean[] getBooleanArrayExtra(String name) {
		try {
			return super.getBooleanArrayExtra(name);
		} catch (Throwable e) {
			return new boolean[0];
		}
	}

	public boolean[] getBooleanArrayExtraReturnNotNull(String name) {
		boolean[] result;
		try {
			result = super.getBooleanArrayExtra(name);
		} catch (Throwable e) {
			return new boolean[0];
		}
		if (result == null) {
			return new boolean[0];
		}
		return result;
	}

	@Override
	public Bundle getBundleExtra(String name) {
		try {
			return super.getBundleExtra(name);
		} catch (Throwable e) {
			return new Bundle();
		}
	}

	public Bundle getBundleExtraReturnNotNull(String name) {
		Bundle bundle;
		try {
			bundle = super.getBundleExtra(name);
		} catch (Throwable e) {
			return new Bundle();
		}
		if (bundle == null) {
			return new Bundle();
		}
		return bundle;
	}

	@Override
	public Bundle getExtras() {
		try {
			return super.getExtras();
		} catch (Throwable e) {
			return new Bundle();
		}
	}

	public Bundle getExtrasReturnNotNull() {
		Bundle bundle;
		try {
			bundle = super.getExtras();
		} catch (Throwable e) {
			return new Bundle();
		}
		if (bundle == null) {
			return new Bundle();
		}
		return bundle;
	}

	@Override
	public Intent putExtra(String name, Bundle value) {
		try {
			return super.putExtra(name, value);
		} catch (Throwable e) {
			return this;
		}
	}

	@Override
	public byte[] getByteArrayExtra(String name) {
		try {
			return super.getByteArrayExtra(name);
		} catch (Throwable e) {
			return new byte[0];
		}
	}


	public byte[] getByteArrayExtraReturnNotNull(String name) {
		byte[] result;
		try {
			result = super.getByteArrayExtra(name);
		} catch (Throwable e) {
			return new byte[0];
		}
		if (result == null) {
			return new byte[0];
		}
		return result;
	}

	@Override
	public char[] getCharArrayExtra(String name) {
		try {
			return super.getCharArrayExtra(name);
		} catch (Throwable e) {
			return new char[0];
		}
	}

	public char[] getCharArrayExtraReturnNotNull(String name) {
		char[] result;
		try {
			result = super.getCharArrayExtra(name);
		} catch (Throwable e) {
			return new char[0];
		}
		if (result == null) {
			return new char[0];
		}
		return result;
	}

	@Override
	public CharSequence[] getCharSequenceArrayExtra(String name) {
		try {
			return super.getCharSequenceArrayExtra(name);
		} catch (Throwable e) {
			return new CharSequence[0];
		}

	}

	public CharSequence[] getCharSequenceArrayExtraReturnNotNull(String name) {
		CharSequence[] result;
		try {
			result = super.getCharSequenceArrayExtra(name);
		} catch (Throwable e) {
			return new CharSequence[0];
		}
		if (result == null) {
			return new CharSequence[0];
		}
		return result;
	}

	@Override
	public double[] getDoubleArrayExtra(String name) {
		try {
			return super.getDoubleArrayExtra(name);
		} catch (Throwable e) {
			return new double[0];
		}
	}

	public double[] getDoubleArrayExtraReturnNotNull(String name) {
		double[] result;
		try {
			result = super.getDoubleArrayExtra(name);
		} catch (Throwable e) {
			return new double[0];
		}
		if (result == null) {
			return new double[0];
		}
		return result;
	}

	@Override
	public float[] getFloatArrayExtra(String name) {
		try {
			return super.getFloatArrayExtra(name);
		} catch (Throwable e) {
			return new float[0];
		}
	}

	public float[] getFloatArrayExtraReturnNotNull(String name) {
		float[] result;
		try {
			result = super.getFloatArrayExtra(name);
		} catch (Throwable e) {
			return new float[0];
		}
		if (result == null) {
			return new float[0];
		}
		return result;
	}

	@Override
	public int[] getIntArrayExtra(String name) {
		try {
			return super.getIntArrayExtra(name);
		} catch (Throwable e) {
			return new int[0];
		}
	}

	public int[] getIntArrayExtraReturnNotNull(String name) {
		int[] result;
		try {
			result = super.getIntArrayExtra(name);
		} catch (Throwable e) {
			return new int[0];
		}
		if (result == null) {
			return new int[0];
		}
		return result;
	}

	@Override
	public long[] getLongArrayExtra(String name) {
		try {
			return super.getLongArrayExtra(name);
		} catch (Throwable e) {
			return new long[0];
		}
	}

	public long[] getLongArrayExtraReturnNotNull(String name) {
		long[] result;
		try {
			result = super.getLongArrayExtra(name);
		} catch (Throwable e) {
			return new long[0];
		}
		if (result == null) {
			return new long[0];
		}
		return result;
	}

	@Override
	public Parcelable[] getParcelableArrayExtra(String name) {
		try {
			return super.getParcelableArrayExtra(name);
		} catch (Throwable e) {
			return new Parcelable[0];
		}
	}

	public Parcelable[] getParcelableArrayExtraReturnNotNull(String name) {
		Parcelable[] result;
		try {
			result = super.getParcelableArrayExtra(name);
		} catch (Throwable e) {
			return new Parcelable[0];
		}
		if (result == null) {
			return new Parcelable[0];
		}
		return result;
	}

	@Override
	public String[] getStringArrayExtra(String name) {

		try {
			return super.getStringArrayExtra(name);
		} catch (Throwable e) {
			return new String[0];
		}
	}

	public String[] getStringArrayExtraReturnNotNull(String name) {
		String[] result;
		try {
			result = super.getStringArrayExtra(name);
		} catch (Throwable e) {
			return new String[0];
		}
		if (result == null) {
			return new String[0];
		}
		return result;
	}

	@Override
	public short getShortExtra(String name, short defaultValue) {
		try {
			return super.getShortExtra(name, defaultValue);
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	@Override
	public short[] getShortArrayExtra(String name) {
		try {
			return super.getShortArrayExtra(name);
		} catch (Throwable e) {
			return new short[0];
		}
	}

	public short[] getShortArrayExtraReturnNotNull(String name) {
		short[] result;
		try {
			result = super.getShortArrayExtra(name);
		} catch (Throwable e) {
			return new short[0];
		}
		if (result == null) {
			return new short[0];
		}
		return result;
	}

	@Override
	public boolean hasExtra(String name) {
		try {
			return super.hasExtra(name);
		} catch (Throwable e) {
			return false;
		}
	}

	@Deprecated
	@Override
	public String toURI() {
		try {
			return super.toURI();
		} catch (Throwable e) {
			LogUtil.e(TAG, "toURI: exception " + e.getMessage());
			return "";
		}
	}

	@Nullable
	@Override
	public String getScheme() {
		try {
			return super.getScheme();
		} catch (Throwable e) {
			return null;
		}
	}

	@Nullable
	@Override
	public Uri getData() {
		try {
			return super.getData();
		} catch (Throwable e) {
			return null;
		}
	}

	@Deprecated
	public String toURIReturnNotNull() {
		String result = EMPTY;
		try {
			result = super.toURI();
		} catch (Throwable e) {
			LogUtil.e(TAG, "toURI: exception " + e.getMessage());
		}
		if (TextUtils.isEmpty(result)) {
			return EMPTY;
		}
		return result;
	}

	/**
	 * 判断是否有DOS攻击
	 *
	 * @return
	 */
	public boolean hasIntentBomb() {
		boolean hasIntentBomb = false;
		try {
			super.getStringExtra("ANYTHING");
		} catch (Throwable e) {
			hasIntentBomb = true;
		}
		if (hasIntentBomb) {
			LogUtil.e(TAG, "hasIntentBomb");
		}
		return hasIntentBomb;
	}
}
