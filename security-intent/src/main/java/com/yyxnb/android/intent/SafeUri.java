package com.yyxnb.android.intent;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * SafeUri
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/4/1
 */
public class SafeUri {
	private static final String TAG = SafeUri.class.getSimpleName();
	private static final String EMPTY = "";

	private SafeUri() {
	}

	public static String getQueryParameter(Uri uri, String key) {
		if (uri == null || TextUtils.isEmpty(key)) {
			return EMPTY;
		}
		try {
			return uri.getQueryParameter(key);
		} catch (Exception e) {
			Log.e(TAG, "getQueryParameter: " + e.getMessage());
			return EMPTY;
		}
	}

	public static List<String> getQueryParameters(Uri uri, String key) {
		List<String> list = new ArrayList<>();
		if (uri == null || TextUtils.isEmpty(key)) {
			return list;
		}

		try {
			return uri.getQueryParameters(key);
		} catch (Exception e) {
			Log.e(TAG, "getQueryParameters: " + e.getMessage());
		}
		return list;
	}

	@SuppressLint("NewApi")
	public static Set<String> getQueryParameterNames(Uri uri) {
		Set<String> stringSet = new LinkedHashSet<>();
		if (uri == null) {
			return stringSet;
		}
		try {
			stringSet = uri.getQueryParameterNames();
		} catch (Exception e) {
			Log.e(TAG, "getQueryParameterNames: " + e.getMessage());
		}
		return stringSet;
	}


	@SuppressLint("NewApi")
	public static boolean getBooleanQueryParameter(Uri uri, String key, boolean defaultValue) {
		if (uri == null || TextUtils.isEmpty(key)) {
			return defaultValue;
		}

		try {
			return uri.getBooleanQueryParameter(key, defaultValue);
		} catch (Exception e) {
			Log.e(TAG, "getBooleanQueryParameter: " + e.getMessage());
			return defaultValue;
		}
	}
}
