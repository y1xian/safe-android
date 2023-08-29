package com.yyxnb.android.secure.encrypt.hash;

import android.os.Build;
import android.text.TextUtils;

import com.yyxnb.android.secure.utils.EncryptUtil;
import com.yyxnb.android.secure.utils.HexUtil;
import com.yyxnb.android.secure.utils.LogUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * PBKDF2算法使用规则：
 * <p>
 * 迭代次数和安全性成正比例，也与计算时间成正比例，迭代次数越大，意味着计算密钥花费时间越长，同时抗暴力破解能力越强，
 * 对于性能不敏感或高安全性要求场景推荐迭代次数至少需要10000000次，
 * <p>
 *  其它场景迭代次数默认推荐至少10000次，对于性能有特殊要求的产品最低可以迭代1000次（NIST SP 800 132）；
 *  哈希函数推荐选择SHA256或更安全的哈希算法；PBKDF2WithHmacSHA256 API
 * 26及以上才支持，参考：https://developer.android.com/reference/javax/crypto/SecretKeyFactory
 *  盐值至少16字节，应使用安全的随机数；
 *  用于口令单向哈希时，其输出长度应该不小于256比特；
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/24
 */
public abstract class PBKDF2 {
	private static final String TAG = PBKDF2.class.getSimpleName();

	private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

	// https://developer.android.com/reference/javax/crypto/SecretKeyFactory
	private static final String PBKDF2_SHA256_ALGORITHM = "PBKDF2WithHmacSHA256";

	private static final String EMPTY = "";

	private static final int SALT_LEN = 8;

	// 密码算法应用规范要求盐值至少16字节
	private static final int NEW_SALT_LEN = 16;

	private static final int HASH_BYTE_SIZE = 32;

	private static final int PBKDF2_ITERATIONS_TIMES = 10000;

	private static final int PBKDF2_MIN_ITERATIONS_TIMES = 1000;

	/**
	 * 对口令进行单向哈希处理，默认迭代次数10000次
	 *
	 * @param password 要哈希的口令
	 * @return 十六进制形式字符串 （盐值十六进制字符串形式+哈希结果十六进制形式）
	 */
	@Deprecated
	public static String pbkdf2Encrypt(String password) {
		return pbkdf2Encrypt(password, PBKDF2_ITERATIONS_TIMES);
	}

	/**
	 * 对口令进行单向哈希处理，自定义迭代次数，但是不能小于1000
	 *
	 * @param password   待哈希的口令
	 * @param iterations 迭代次数
	 * @return 十六进制形式字符串 （盐值十六进制字符串形式+哈希结果十六进制形式）
	 */
	@Deprecated
	public static String pbkdf2Encrypt(String password, int iterations) {
		byte[] salt = EncryptUtil.generateSecureRandom(SALT_LEN);
		return pbkdf2Encrypt(password, salt, iterations, HASH_BYTE_SIZE);
	}

	/**
	 * @param password   待哈希的口令
	 * @param salt       盐值，需使用安全随机数生成，长度不低于 8字节
	 * @param iterations 迭代次数，不低于1000次
	 * @param cipherLen  输出长度，不低于32字节
	 * @return 十六进制形式字符串 （盐值十六进制字符串形式+哈希结果十六进制形式）
	 */
	@Deprecated
	public static String pbkdf2Encrypt(String password, byte[] salt, int iterations, int cipherLen) {
		if (TextUtils.isEmpty(password)) {
			LogUtil.e(TAG, "pwd is null.");
			return EMPTY;
		}

		if (iterations < PBKDF2_MIN_ITERATIONS_TIMES) {
			LogUtil.e(TAG, "iterations times is not enough.");
			return EMPTY;
		}

		if (salt == null || salt.length < SALT_LEN) {
			LogUtil.e(TAG, "salt parameter is null or length is not enough");
			return EMPTY;
		}

		if (cipherLen < HASH_BYTE_SIZE) {
			LogUtil.e(TAG, "cipherLen length is not enough");
			return EMPTY;
		}

		byte[] hash = pbkdf2(password.toCharArray(), salt, iterations, cipherLen * 8);
		return HexUtil.byteArray2HexStr(salt) + HexUtil.byteArray2HexStr(hash);
	}

	/**
	 * 口令验证方法，默认迭代次数10000次
	 *
	 * @param password        明文口令
	 * @param encryptPassword 哈希后的口令
	 * @return 验证口令是否正确 boolean
	 */
	@Deprecated
	public static boolean validatePassword(String password, String encryptPassword) {
		return validatePassword(password, encryptPassword, PBKDF2_ITERATIONS_TIMES);
	}

	/**
	 * 口令验证方法，指定迭代次数
	 *
	 * @param password        明文口令
	 * @param encryptPassword 哈希后的口令
	 * @param iterations      迭代次数
	 * @return 验证口令是否正确 boolean
	 */
	@Deprecated
	public static boolean validatePassword(String password, String encryptPassword, int iterations) {
		if (TextUtils.isEmpty(password) || TextUtils.isEmpty(encryptPassword)
				|| encryptPassword.length() < SALT_LEN * 2) {
			return false;
		}
		String salt = encryptPassword.substring(0, SALT_LEN * 2);
		String hash = encryptPassword.substring(SALT_LEN * 2);

		byte[] realHash = pbkdf2(password.toCharArray(), HexUtil.hexStr2ByteArray(salt), iterations,
				HASH_BYTE_SIZE * 8);
		byte[] tempHash = HexUtil.hexStr2ByteArray(hash);
		return isByteArrayEqual(realHash, tempHash);

	}

	/**
	 * pbkdf2的核心函数
	 *
	 * @param password
	 * @param salt
	 * @param iterations
	 * @param bytes
	 * @return
	 */
	public static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) {
		return pbkdf(password, salt, iterations, bytes, false);
	}

	// @RequiresApi(api = Build.VERSION_CODES.O)
	public static byte[] pbkdf2SHA256(char[] password, byte[] salt, int iterations, int bytes) {
		byte[] result = new byte[0];
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			LogUtil.e(TAG, "system version not high than 26");
			return result;
		}
		return pbkdf(password, salt, iterations, bytes, true);
	}

	private static byte[] pbkdf(char[] password, byte[] salt, int iterations, int bytes, boolean isSHA256) {
		try {
			SecretKeyFactory skf = null;
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes);
			if (isSHA256) {
				skf = SecretKeyFactory.getInstance(PBKDF2_SHA256_ALGORITHM);
			} else {
				skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
			}
			return skf.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			LogUtil.e(TAG, "pbkdf exception : " + e.getMessage());
		}
		return new byte[0];
	}

	/**
	 * 比较两个字节数组是否完全相等
	 *
	 * @param realHash
	 * @param tempHash
	 * @return
	 */
	private static boolean isByteArrayEqual(byte[] realHash, byte[] tempHash) {

		if (realHash == null || tempHash == null) {
			return false;
		}
		int diff = realHash.length ^ tempHash.length;
		for (int i = 0; i < realHash.length && i < tempHash.length; i++) {
			diff |= realHash[i] ^ tempHash[i];
		}
		return diff == 0;
	}

	/**
	 * 对口令进行单向哈希处理，默认迭代次数10000次
	 *
	 * @param password 要哈希的口令
	 * @return 十六进制形式字符串 （盐值十六进制字符串形式+哈希结果十六进制形式）
	 */
	public static String pbkdf2EncryptNew(String password) {
		return pbkdf2EncryptNew(password, PBKDF2_ITERATIONS_TIMES);
	}

	/**
	 * 对口令进行单向哈希处理，自定义迭代次数，但是不能小于1000
	 *
	 * @param password   待哈希的口令
	 * @param iterations 迭代次数
	 * @return 十六进制形式字符串 （盐值十六进制字符串形式+哈希结果十六进制形式）
	 */
	public static String pbkdf2EncryptNew(String password, int iterations) {
		byte[] salt = EncryptUtil.generateSecureRandom(NEW_SALT_LEN);
		return pbkdf2EncryptNew(password, salt, iterations, HASH_BYTE_SIZE);
	}

	/**
	 * @param password   待哈希的口令
	 * @param salt       盐值，需使用安全随机数生成，长度不低于 8字节
	 * @param iterations 迭代次数，不低于1000次
	 * @param cipherLen  输出长度，不低于32字节
	 * @return 十六进制形式字符串 （盐值十六进制字符串形式+哈希结果十六进制形式）
	 */
	public static String pbkdf2EncryptNew(String password, byte[] salt, int iterations, int cipherLen) {
		if (TextUtils.isEmpty(password)) {
			LogUtil.e(TAG, "pwd is null.");
			return EMPTY;
		}

		if (iterations < PBKDF2_MIN_ITERATIONS_TIMES) {
			LogUtil.e(TAG, "iterations times is not enough.");
			return EMPTY;
		}

		if (salt == null || salt.length < NEW_SALT_LEN) {
			LogUtil.e(TAG, "salt parameter is null or length is not enough");
			return EMPTY;
		}

		if (cipherLen < HASH_BYTE_SIZE) {
			LogUtil.e(TAG, "cipherLen length is not enough");
			return EMPTY;
		}

		byte[] hash;
		// https://developer.android.com/reference/javax/crypto/SecretKeyFactory
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			LogUtil.i(TAG, "sha 1");
			hash = pbkdf2(password.toCharArray(), salt, iterations, cipherLen * 8);
		} else {
			LogUtil.i(TAG, "sha 256");
			hash = pbkdf2SHA256(password.toCharArray(), salt, iterations, cipherLen * 8);
		}
		return HexUtil.byteArray2HexStr(salt) + HexUtil.byteArray2HexStr(hash);
	}

	/**
	 * 口令验证方法，默认迭代次数10000次
	 *
	 * @param password        明文口令
	 * @param encryptPassword 哈希后的口令
	 * @return 验证口令是否正确 boolean
	 */
	public static boolean validatePasswordNew(String password, String encryptPassword) {
		return validatePasswordNew(password, encryptPassword, PBKDF2_ITERATIONS_TIMES);
	}

	/**
	 * 口令验证方法，指定迭代次数
	 *
	 * @param password        明文口令
	 * @param encryptPassword 哈希后的口令
	 * @param iterations      迭代次数
	 * @return 验证口令是否正确 boolean
	 */
	public static boolean validatePasswordNew(String password, String encryptPassword, int iterations) {
		if (TextUtils.isEmpty(password) || TextUtils.isEmpty(encryptPassword)
				|| encryptPassword.length() < NEW_SALT_LEN * 2) {
			return false;
		}
		String salt = encryptPassword.substring(0, NEW_SALT_LEN * 2);
		String hash = encryptPassword.substring(NEW_SALT_LEN * 2);

		byte[] realHash;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			realHash = pbkdf2(password.toCharArray(), HexUtil.hexStr2ByteArray(salt), iterations, HASH_BYTE_SIZE * 8);
		} else {
			realHash = pbkdf2SHA256(password.toCharArray(), HexUtil.hexStr2ByteArray(salt), iterations,
					HASH_BYTE_SIZE * 8);
		}
		byte[] tempHash = HexUtil.hexStr2ByteArray(hash);
		return isByteArrayEqual(realHash, tempHash);
	}
}
