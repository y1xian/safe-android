package com.yyxnb.android.secure.encrypt.hash;

import android.text.TextUtils;

import com.yyxnb.android.secure.utils.HexUtil;
import com.yyxnb.android.secure.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA算法
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/24
 */
public final class SHA {
	private static final String TAG = SHA.class.getSimpleName();

	private static final String SHA256_ALGORITHM = "SHA-256";

	private static final String EMPTY = "";

	private static final String[] SAFE_ALGORITHM = {"SHA-256", "SHA-384", "SHA-512"};

	private SHA() {

	}

	/**
	 * SHA256算法
	 *
	 * @param content 待取摘要的内容
	 * @return 摘要值 （十六进制字符串形式）
	 */
	public static String sha256Encrypt(String content) {
		return shaEncrypt(content, SHA256_ALGORITHM);
	}

	/**
	 * SHA算法
	 *
	 * @param content   待取摘要的内容
	 * @param algorithm 指定哈希算法，可指定SHA-256，SHA-384，SHA-512
	 * @return 摘要值 （十六进制字符串形式）
	 */
	public static String shaEncrypt(String content, String algorithm) {

		if (TextUtils.isEmpty(content) || TextUtils.isEmpty(algorithm)) {
			LogUtil.e(TAG, "content or algorithm is null.");
			return EMPTY;
		}
		if (!isLegalAlgorithm(algorithm)) {
			LogUtil.e(TAG, "algorithm is not safe or legal");
			return EMPTY;
		}
		byte[] contentBytes;
		try {
			contentBytes = content.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			contentBytes = new byte[0];
			LogUtil.e(TAG, "Error in generate SHA UnsupportedEncodingException");
		}
		byte[] encrypt = shaEncryptByte(contentBytes, algorithm);
		return HexUtil.byteArray2HexStr(encrypt);
	}

	/**
	 * SHA算法
	 *
	 * @param content   待取摘要的内容
	 * @param algorithm 指定哈希算法，可指定SHA-256，SHA-384，SHA-512
	 * @return 摘要值 （字节数组形式）
	 */
	public static byte[] shaEncryptByte(byte[] content, String algorithm) {
		if (content == null || TextUtils.isEmpty(algorithm)) {
			LogUtil.e(TAG, "content or algorithm is null.");
			return new byte[0];
		}
		if (!isLegalAlgorithm(algorithm)) {
			LogUtil.e(TAG, "algorithm is not safe or legal");
			return new byte[0];
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(content);
			return messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "Error in generate SHA NoSuchAlgorithmException");
		}
		return new byte[0];
	}

	/**
	 * 完整性校验
	 *
	 * @param content        明文
	 * @param encryptContent 哈希之后的值
	 * @return 校验是否通过 boolean
	 */
	public static boolean validateSHA256(String content, String encryptContent) {
		if (TextUtils.isEmpty(content) || TextUtils.isEmpty(encryptContent)) {
			return false;
		}
		return encryptContent.equals(sha256Encrypt(content));
	}

	/**
	 * 完整性校验
	 *
	 * @param content        明文
	 * @param encryptContent 哈希后的值
	 * @param algorithm      哈希算法，可指定SHA-256，SHA-384，SHA-512
	 * @return 校验是否通过 boolean
	 */
	public static boolean validateSHA(String content, String encryptContent, String algorithm) {
		if (TextUtils.isEmpty(content) || TextUtils.isEmpty(encryptContent) || TextUtils.isEmpty(algorithm)) {
			return false;
		}
		return encryptContent.equals(shaEncrypt(content, algorithm));
	}

	/**
	 * 判断是否是合法的算法，当前SHA-256，SHA-384，SHA512是合法的算法，白名单控制
	 *
	 * @param algorithm
	 * @return
	 */
	private static boolean isLegalAlgorithm(String algorithm) {
		for (String alg : SAFE_ALGORITHM) {
			if (alg.equals(algorithm)) {
				return true;
			}
		}
		return false;
	}
}
