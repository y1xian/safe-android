package com.yyxnb.android.encrypt.rsa;

import android.text.TextUtils;
import android.util.Base64;

import com.yyxnb.android.encrypt.utils.EncryptUtil;
import com.yyxnb.android.encrypt.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA加密和解密，RSA/ECB/OAEPWithSHA-256AndMGF1Padding
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/26
 */
public abstract class RSAEncrypt {
	private static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

	private static final String TAG = "RSAEncrypt";

	private static final String CHARSET = "UTF-8";

	private static final String EMPTY = "";

	private static final int KEY_LENGTH = 2048;

	private static final String RSA = "RSA";

	/**
	 * RSA加密，公钥加密
	 *
	 * @param data         待加密数据
	 * @param publicKeyStr 字符串形式公钥
	 * @return 加密后数据 string
	 */
	public static String encrypt(String data, String publicKeyStr) {
		if (TextUtils.isEmpty(data) || TextUtils.isEmpty(publicKeyStr)) {
			LogUtil.e(TAG, "content or public key is null");
			return EMPTY;
		}
		PublicKey publicKey = EncryptUtil.getPublicKey(publicKeyStr);
		return encrypt(data, publicKey);
	}

	/**
	 * RSA解密，私钥解密
	 *
	 * @param data          待解密数据
	 * @param privateKeyStr 解密私钥
	 * @return 解密后的结果 string
	 */
	public static String decrypt(String data, String privateKeyStr) {
		if (TextUtils.isEmpty(data) || TextUtils.isEmpty(privateKeyStr)) {
			LogUtil.e(TAG, "content or private key is null");
			return EMPTY;
		}
		PrivateKey privateKey = EncryptUtil.getPrivateKey(privateKeyStr);
		return decrypt(data, privateKey);
	}

	/**
	 * RSA加密，公钥加密
	 *
	 * @param data      待加密数据
	 * @param publicKey 加密公钥
	 * @return 加密后数据 ，每次加密结果都不一样，即使是相同的公钥和明文
	 */
	public static String encrypt(String data, PublicKey publicKey) {
		if (TextUtils.isEmpty(data) || publicKey == null || !isPublicKeyLengthRight((RSAPublicKey) publicKey)) {
			LogUtil.e(TAG, "content or PublicKey is null , or length is too short");
			return EMPTY;
		}
		try {
			return Base64.encodeToString(encrypt(data.getBytes(CHARSET), publicKey), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "encrypt: UnsupportedEncodingException");
		} catch (Exception e) {
			LogUtil.e(TAG, "exception : " + e.getMessage());
		}
		return EMPTY;
	}

	/**
	 * RSA解密，私钥解密
	 *
	 * @param data       待解密数据
	 * @param privateKey 解密私钥
	 * @return 解密后的结果 string
	 */
	public static String decrypt(String data, PrivateKey privateKey) {
		if (TextUtils.isEmpty(data) || privateKey == null || !isPrivateKeyLengthRight((RSAPrivateKey) privateKey)) {
			LogUtil.e(TAG, "content or privateKey is null , or length is too short");
			return EMPTY;
		}
		try {
			return new String(decrypt(Base64.decode(data, Base64.DEFAULT), privateKey), CHARSET);
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "RSA decrypt exception : " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "exception : " + e.getMessage());
		}
		return EMPTY;
	}

	/**
	 * RSA加密，公钥加密
	 *
	 * @param data      待加密数据
	 * @param publicKey 加密公钥
	 * @return 加密后数据 ，每次加密结果都不一样，即使是相同的公钥和明文
	 */
	public static byte[] encrypt(byte[] data, PublicKey publicKey) {
		byte[] result = new byte[0];
		if (data == null || publicKey == null || !isPublicKeyLengthRight((RSAPublicKey) publicKey)) {
			LogUtil.e(TAG, "content or PublicKey is null , or length is too short");
			return result;
		}
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher.doFinal(data);
		} catch (GeneralSecurityException e) {
			LogUtil.e(TAG, "RSA encrypt exception : " + e.getMessage());
		}
		return result;
	}

	/**
	 * RSA解密，私钥解密
	 *
	 * @param data       待解密数据
	 * @param privateKey 解密私钥
	 * @return 解密后的结果 string
	 */
	public static byte[] decrypt(byte[] data, PrivateKey privateKey) {
		byte[] result = new byte[0];
		if (data == null || privateKey == null || !isPrivateKeyLengthRight((RSAPrivateKey) privateKey)) {
			LogUtil.e(TAG, "content or privateKey is null , or length is too short");
			return result;
		}
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			result = cipher.doFinal(data);
		} catch (GeneralSecurityException e) {
			LogUtil.e(TAG, "RSA decrypt exception : " + e.getMessage());
		}
		return result;
	}

	/**
	 * 检查公钥长度
	 *
	 * @param rsaPublicKey 公钥
	 * @return
	 */
	public static boolean isPublicKeyLengthRight(RSAPublicKey rsaPublicKey) {
		if (rsaPublicKey == null) {
			return false;
		}
		return rsaPublicKey.getModulus().bitLength() >= KEY_LENGTH;
	}

	/**
	 * 检查私钥长度
	 *
	 * @param rsaPrivateKey 私钥
	 * @return
	 */
	public static boolean isPrivateKeyLengthRight(RSAPrivateKey rsaPrivateKey) {
		if (rsaPrivateKey == null) {
			return false;
		}
		return rsaPrivateKey.getModulus().bitLength() >= KEY_LENGTH;
	}

	/**
	 * 生成公私钥对
	 *
	 * @return 公私钥对
	 * @throws NoSuchAlgorithmException
	 */

	public static Map<String, Key> generateRSAKeyPair(int length) throws NoSuchAlgorithmException {
		Map<String, Key> map = new HashMap<>(2);
		if (length < KEY_LENGTH) {
			LogUtil.e(TAG, "generateRSAKeyPair: key length is too short");
			return map;
		}
		SecureRandom sr = EncryptUtil.genSecureRandom();
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
		kpg.initialize(length, sr);
		KeyPair kp = kpg.generateKeyPair();
		Key publicKey = kp.getPublic();
		Key privateKey = kp.getPrivate();
		map.put("publicKey", publicKey);
		map.put("privateKey", privateKey);
		return map;
	}
}
