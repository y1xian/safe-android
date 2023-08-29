package com.yyxnb.android.secure.encrypt.hash;

import android.text.TextUtils;

import com.yyxnb.android.secure.utils.HexUtil;
import com.yyxnb.android.secure.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 消息认证算法
 * <p>
 * 对于HMAC,如果其所使用的HASH算法的输出长度大于128bit，则密钥长度至少为HASH算法输出长度，
 * 如果其所使用的HASH算法的输出长度小于128bit，则密钥长度至少为128 bit。
 * <p>
 * SHA256算法的输出长度为256bit，因此HMAC算法的密钥长度应为256bit，32byte
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/24
 */
public abstract class HMACSHA256 {
	private static final String TAG = HMACSHA256.class.getSimpleName();

	private static final String ALGORITHM = "HmacSHA256";

	private static final String EMPTY = "";

	private static final int HMACSHA256_KEY_LEN = 32;

	/**
	 * HMACSHA256算法
	 *
	 * @param content 待哈希内容
	 * @param key     密钥（十六进制字符串形式）
	 * @return 哈希结果 （十六进制形式字符串）
	 */
	public static String hmacSHA256Encrypt(String content, String key) {
		if (TextUtils.isEmpty(content) || TextUtils.isEmpty(key)) {
			return EMPTY;
		}
		byte[] secretKey = HexUtil.hexStr2ByteArray(key);
		return hmacSHA256Encrypt(content, secretKey);
	}

	/**
	 * HMACSHA256算法
	 *
	 * @param content 待哈希内容
	 * @param key     密钥（字节数组形式）
	 * @return 哈希结果 （十六进制形式字符串）
	 */
	public static String hmacSHA256Encrypt(String content, byte[] key) {
		if (TextUtils.isEmpty(content) || key == null) {
			return EMPTY;
		}

		if (key.length < HMACSHA256_KEY_LEN) {
			LogUtil.e(TAG, "hmac key length is not right");
			return EMPTY;
		}
		byte[] contentBytes;
		try {
			contentBytes = content.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			contentBytes = new byte[0];
			LogUtil.e(TAG, "hmacsha256 encrypt exception" + e.getMessage());
		}
		byte[] encrypt = hmacEncrypt(contentBytes, key);
		return HexUtil.byteArray2HexStr(encrypt);
	}

	/**
	 * HMAC算法
	 *
	 * @param content 待哈希内容
	 * @param key     密钥（字节数组形式）
	 * @return 结果 (字节数组形式)
	 */
	public static byte[] hmacEncrypt(byte[] content, byte[] key) {
		if (content == null || key == null) {
			LogUtil.e(TAG, "content or key is null.");
			return new byte[0];
		}
		if (key.length < HMACSHA256_KEY_LEN) {
			LogUtil.e(TAG, "hmac key length is not right");
			return new byte[0];
		}
		try {
			SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			return mac.doFinal(content);
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "hmacsha256 encrypt exception" + e.getMessage());
			return new byte[0];
		}
	}
}
