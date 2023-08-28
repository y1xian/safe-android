package com.yyxnb.android.encrypt.utils;

import android.text.TextUtils;

import com.yyxnb.android.encrypt.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * 常用十六进制转换工具类
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/24
 */
public final class HexUtil {
	private static final String EMPTY = "";

	private static final String TAG = HexUtil.class.getSimpleName();

	/**
	 * 私有构造方法
	 */
	private HexUtil() {
	}

	/**
	 * 字节数组转换为十六进制字符串
	 *
	 * @param bytes 字节数组
	 * @return String 字符串
	 */

	public static String byteArray2HexStr(byte[] bytes) {
		if (null == bytes || bytes.length == 0) {
			return EMPTY;
		}

		StringBuilder sb = new StringBuilder();
		for (byte aByte : bytes) {
			String hex = Integer.toHexString(0xFF & aByte);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 字符串形式的字节数组转换为十六进制形式字符串
	 *
	 * @param sourceStr 字符串
	 * @return 字符串
	 */

	public static String byteArray2HexStr(String sourceStr) {
		String resultStr = "";
		if (TextUtils.isEmpty(sourceStr)) {
			return resultStr;
		}
		try {
			resultStr = byteArray2HexStr(sourceStr.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "byte array 2 hex string exception : " + e.getMessage());
		}
		return resultStr;
	}

	/**
	 * 十六进制形式字符串转换为字节数组
	 *
	 * @param str 字符串
	 * @return byte[] 字节数组
	 */
	public static byte[] hexStr2ByteArray(String str) {
		if (TextUtils.isEmpty(str)) {
			return new byte[0];
		}
		try {
			str = str.toUpperCase(Locale.ENGLISH);
		} catch (Throwable throwable) {
			LogUtil.e(TAG, "hex string toUpperCase exception : " + throwable.getMessage());
			return new byte[0];
		}
		byte[] bytes = new byte[str.length() / 2];
		byte[] source;
		try {
			source = str.getBytes("UTF-8");
			for (int i = 0; i < bytes.length; ++i) {
				byte bh = Byte.decode("0x" + new String(new byte[]{source[i * 2]}, "UTF-8")).byteValue();
				bh = (byte) (bh << 4);
				byte bl = Byte.decode("0x" + new String(new byte[]{source[i * 2 + 1]}, "UTF-8")).byteValue();
				bytes[i] = (byte) (bh ^ bl);
			}
		} catch (UnsupportedEncodingException | NumberFormatException e) {
			LogUtil.e(TAG, "hex string 2 byte array exception : " + e.getMessage());
			return new byte[0];
		}
		return bytes;
	}
}
