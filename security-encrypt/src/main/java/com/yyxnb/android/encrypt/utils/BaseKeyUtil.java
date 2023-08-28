package com.yyxnb.android.encrypt.utils;

import android.annotation.SuppressLint;

import com.yyxnb.android.encrypt.hash.PBKDF2;
import com.yyxnb.android.encrypt.utils.LogUtil;

/**
 * 导出根密钥基础类
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/24
 */
public class BaseKeyUtil {
	private static final String TAG = BaseKeyUtil.class.getSimpleName();
	private static final int ROOT_KEY_COMP_MIN_VALID_LENGTH = 16; // 密钥材料最小长度为16字节\
	private static final int ROOT_KEY_LEN = 16;
	private static final int ITERATION_COUNT = 10000;
	private static final int ROOT_KEY_LEN_32 = 32; // 导出长度32字节
	private static final int ITERATION_COUNT_1 = 1; // 迭代次数1，满足安全要求

	/**
	 * 三段根密钥组件+ 盐值 导出根密钥(导出长度16，迭代次数10000)
	 *
	 * @param first    第一段 Hex String 形式根密钥组件（长度至少32）
	 * @param second   第二段 Hex String 形式根密钥组件（长度至少32）
	 * @param third    第三段 Hex String 形式根密钥组件（长度至少32）
	 * @param salt     盐值，长度最少16
	 * @param isSHA256 是否使用SHA256 ，注意：PBKDF2WithHmacSHA256 算法在Android
	 *                 O及以上版本才支持，调用者需要自行判断再调用
	 * @return
	 */
	@SuppressLint("NewApi")
	public static byte[] exportRootKey(String first, String second, String third, byte[] salt, boolean isSHA256) {
		return exportRootKey(first, second, third, salt, ROOT_KEY_LEN, isSHA256);
	}

	/**
	 * 三段根密钥组件+ 盐值 导出根密钥(导出长度32，迭代次数10000)
	 *
	 * @param first    第一段 Hex String 形式根密钥组件（长度至少32）
	 * @param second   第二段 Hex String 形式根密钥组件（长度至少32）
	 * @param third    第三段 Hex String 形式根密钥组件（长度至少32）
	 * @param salt     盐值，长度最少16
	 * @param isSHA256 是否使用SHA256 ，注意：PBKDF2WithHmacSHA256 算法在Android
	 *                 O及以上版本才支持，调用者需要自行判断再调用
	 * @return
	 */
	public static byte[] exportRootKey32(String first, String second, String third, byte[] salt, boolean isSHA256) {
		return exportRootKey(first, second, third, salt, ROOT_KEY_LEN_32, isSHA256);
	}

	/**
	 * 三段根密钥组件+ 盐值 导出根密钥(导出长度16，迭代次数1)
	 *
	 * @param first    第一段 Hex String 形式根密钥组件（长度至少32）
	 * @param second   第二段 Hex String 形式根密钥组件（长度至少32）
	 * @param third    第三段 Hex String 形式根密钥组件（长度至少32）
	 * @param salt     盐值，长度最少16
	 * @param isSHA256 是否使用SHA256 ，注意：PBKDF2WithHmacSHA256 算法在Android
	 *                 O及以上版本才支持，调用者需要自行判断再调用
	 * @return
	 */
	@SuppressLint("NewApi")
	public static byte[] exportRootKeyIteration1(String first, String second, String third, byte[] salt,
												 boolean isSHA256) {
		return exportRootKey(first, second, third, salt, ITERATION_COUNT_1, ROOT_KEY_LEN, isSHA256);
	}

	/**
	 * 三段根密钥组件+ 盐值 导出根密钥(导出长度32，迭代次数1)
	 *
	 * @param first    第一段 Hex String 形式根密钥组件（长度至少32）
	 * @param second   第二段 Hex String 形式根密钥组件（长度至少32）
	 * @param third    第三段 Hex String 形式根密钥组件（长度至少32）
	 * @param salt     盐值，长度最少16
	 * @param isSHA256 是否使用SHA256 ，注意：PBKDF2WithHmacSHA256 算法在Android
	 *                 O及以上版本才支持，调用者需要自行判断再调用
	 * @return
	 */
	public static byte[] exportRootKey32Iteration1(String first, String second, String third, byte[] salt,
												   boolean isSHA256) {
		return exportRootKey(first, second, third, salt, ITERATION_COUNT_1, ROOT_KEY_LEN_32, isSHA256);
	}

	/**
	 * 三段根密钥组件+ 盐值 导出根密钥
	 *
	 * @param first     第一段 Hex String 形式根密钥组件（长度至少32）
	 * @param second    第二段 Hex String 形式根密钥组件（长度至少32）
	 * @param third     第三段 Hex String 形式根密钥组件（长度至少32）
	 * @param salt      盐值，长度最少16
	 * @param exportLen 导出长度，16或者32
	 * @param isSHA256  是否使用SHA256 ，注意：PBKDF2WithHmacSHA256 算法在Android
	 *                  O及以上版本才支持，调用者需要自行判断再调用
	 * @return
	 */
	@SuppressLint("NewApi")
	public static byte[] exportRootKey(String first, String second, String third, byte[] salt, int exportLen,
									   boolean isSHA256) {
		return exportRootKey(first, second, third, salt, ITERATION_COUNT, exportLen, isSHA256);
	}

	/**
	 * 三段根密钥组件+ 盐值 导出根密钥
	 *
	 * @param first     第一段 Hex String 形式根密钥组件（长度至少32）
	 * @param second    第二段 Hex String 形式根密钥组件（长度至少32）
	 * @param third     第三段 Hex String 形式根密钥组件（长度至少32）
	 * @param salt      盐值，长度最少16
	 * @param iteration 迭代次数
	 * @param exportLen 导出长度，16或者32
	 * @param isSHA256  是否使用SHA256 ，注意：PBKDF2WithHmacSHA256 算法在Android
	 *                  O及以上版本才支持，调用者需要自行判断再调用
	 * @return
	 */
	public static byte[] exportRootKey(String first, String second, String third, byte[] salt, int iteration,
									   int exportLen, boolean isSHA256) {
		byte[] c1 = HexUtil.hexStr2ByteArray(first);
		byte[] c2 = HexUtil.hexStr2ByteArray(second);
		byte[] c3 = HexUtil.hexStr2ByteArray(third);
		int len = getMinValue(c1.length, c2.length, c3.length);

		if (!isKeyAndSaltValid(len, salt)) {
			throw new IllegalArgumentException("key length must be more than 128bit.");
		}
		char[] combined = new char[len];
		for (int i = 0; i < len; i++) {
			combined[i] = (char) (c1[i] ^ c2[i] ^ c3[i]);
		}

		// https://developer.android.com/reference/javax/crypto/SecretKeyFactory
		if (!isSHA256) {
			LogUtil.i(TAG, "exportRootKey: sha1");
			return PBKDF2.pbkdf2(combined, salt, iteration, exportLen * 8);
		} else {
			LogUtil.i(TAG, "exportRootKey: sha256");
			return PBKDF2.pbkdf2SHA256(combined, salt, iteration, exportLen * 8);
		}
	}

	/**
	 * 三段根密钥组件+ 盐值 导出根密钥
	 *
	 * @param first     第一段 Hex String 形式根密钥组件（长度至少32）
	 * @param second    第二段 Hex String 形式根密钥组件（长度至少32）
	 * @param third     第三段 Hex String 形式根密钥组件（长度至少32）
	 * @param salt      Hex String 形式盐值，长度最少32
	 * @param exportLen 导出长度，16或者32
	 * @param isSHA256  是否使用SHA256 ，注意：PBKDF2WithHmacSHA256 算法在Android
	 *                  O及以上版本才支持，调用者需要自行判断再调用
	 * @return
	 */
	public static byte[] exportRootKey(String first, String second, String third, String salt, int exportLen,
									   boolean isSHA256) {
		return exportRootKey(first, second, third, HexUtil.hexStr2ByteArray(salt), exportLen, isSHA256);
	}

	/**
	 * 三段根密钥组件+ 盐值 导出根密钥
	 *
	 * @param first     第一段 Hex String 形式根密钥组件（长度至少32）
	 * @param second    第二段 Hex String 形式根密钥组件（长度至少32）
	 * @param third     第三段 Hex String 形式根密钥组件（长度至少32）
	 * @param salt      盐值，长度最少16
	 * @param exportLen 导出长度，16或者32
	 * @param isSHA256  是否使用SHA256 ，注意：PBKDF2WithHmacSHA256 算法在Android
	 *                  O及以上版本才支持，调用者需要自行判断再调用
	 * @return
	 */
	public static String exportHexRootKey(String first, String second, String third, byte[] salt, int exportLen,
										  boolean isSHA256) {
		return HexUtil.byteArray2HexStr(exportRootKey(first, second, third, salt, exportLen, isSHA256));
	}

	private static int getMinValue(int len1, int len2, int len3) {
		int min = len1;
		if (len2 < min) {
			min = len2;
		}
		if (len3 < min) {
			min = len3;
		}
		return min;
	}

	private static boolean isKeyAndSaltValid(int keyLength, byte[] salt) {
		boolean retVal1 = isKeyLengthValid(keyLength);
		boolean retVal2 = isKeyLengthValid(salt);
		return retVal1 & retVal2;
	}

	private static boolean isKeyLengthValid(byte[] keyByte) {
		return keyByte.length >= ROOT_KEY_COMP_MIN_VALID_LENGTH;
	}

	private static boolean isKeyLengthValid(int keyLength) {
		return keyLength >= ROOT_KEY_COMP_MIN_VALID_LENGTH;
	}
}
