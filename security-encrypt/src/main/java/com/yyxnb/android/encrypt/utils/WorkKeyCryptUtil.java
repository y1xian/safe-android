package com.yyxnb.android.encrypt.utils;

import com.yyxnb.android.encrypt.aes.AesCbc;
import com.yyxnb.android.encrypt.aes.AesGcm;

/**
 * 使用根密钥对象对工作密钥使用AES/CBC 或者 AES/GCM 算法进行加解密
 * gcm模式 @RequiresApi(api = Build.VERSION_CODES.KITKAT)
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/25
 */
public class WorkKeyCryptUtil {

	private WorkKeyCryptUtil() {
	}

	/**
	 * 使用根密钥对工作密钥加密
	 *
	 * @param plainWorkKey 工作密钥明文， 工作密钥的明文要求是安全随机数生成
	 * @param rootKey      根密钥
	 * @return 加密的工作密钥
	 */
	public static String encryptWorkKey(String plainWorkKey, RootKeyUtil rootKey) {
		return AesCbc.encrypt(plainWorkKey, rootKey.getRootKey());
	}

	public static String encryptWorkKey(String plainWorkKey, byte[] rootKey) {
		return AesCbc.encrypt(plainWorkKey, rootKey);
	}

	/**
	 * 使用根密钥对工作密钥加密，GCM模式
	 *
	 * @param plainWorkKey 明文， 工作密钥的明文要求是安全随机数生成
	 * @param rootKey      根密钥
	 * @return 加密的根密钥
	 */

	public static String encryptWorkKeyGcm(String plainWorkKey, RootKeyUtil rootKey) {
		return AesGcm.encrypt(plainWorkKey, rootKey.getRootKey());
	}


	public static String encryptWorkKeyGcm(String plainWorkKey, byte[] rootKey) {
		return AesGcm.encrypt(plainWorkKey, rootKey);
	}

	/**
	 * 使用根密钥解密加密的工作密钥
	 *
	 * @param enWorkKey 加密的工作密钥
	 * @param rootKey   根密钥
	 * @return 明文的工作密钥
	 */
	public static String decryptWorkKey(String enWorkKey, RootKeyUtil rootKey) {
		return AesCbc.decrypt(enWorkKey, rootKey.getRootKey());
	}

	public static String decryptWorkKey(String enWorkKey, byte[] rootKey) {
		return AesCbc.decrypt(enWorkKey, rootKey);
	}

	/**
	 * 使用根密钥解密加密的工作密钥
	 *
	 * @param enWorkKey 加密的工作密钥
	 * @param rootKey   根密钥
	 * @return 明文的工作密钥
	 */

	public static String decryptWorkKeyGcm(String enWorkKey, RootKeyUtil rootKey) {
		return AesGcm.decrypt(enWorkKey, rootKey.getRootKey());
	}


	public static String decryptWorkKeyGcm(String enWorkKey, byte[] rootKey) {
		return AesGcm.decrypt(enWorkKey, rootKey);
	}

	/**
	 * 加密工作密钥
	 *
	 * @param plainWorkKey 明文工作密钥
	 * @param rootKeyUtil  根密钥
	 * @param iv           IV向量
	 * @return 加密的工作密钥
	 */
	public static byte[] encryptWorkKey2Byte(byte[] plainWorkKey, RootKeyUtil rootKeyUtil, byte[] iv) {
		return AesCbc.encrypt(plainWorkKey, rootKeyUtil.getRootKey(), iv);
	}

	public static byte[] encryptWorkKey2Byte(byte[] plainWorkKey, byte[] rootKeyUtil, byte[] iv) {
		return AesCbc.encrypt(plainWorkKey, rootKeyUtil, iv);
	}

	/**
	 * 加密工作密钥
	 *
	 * @param plainWorkKey 明文工作密钥
	 * @param rootKeyUtil  根密钥
	 * @param iv           IV向量
	 * @return 加密的工作密钥
	 */

	public static byte[] encryptWorkKey2ByteGcm(byte[] plainWorkKey, RootKeyUtil rootKeyUtil, byte[] iv) {
		return AesGcm.encrypt(plainWorkKey, rootKeyUtil.getRootKey(), iv);
	}


	public static byte[] encryptWorkKey2ByteGcm(byte[] plainWorkKey, byte[] rootKeyUtil, byte[] iv) {
		return AesGcm.encrypt(plainWorkKey, rootKeyUtil, iv);
	}

	/**
	 * 解密 加密的工作密钥
	 *
	 * @param enWorkKey   加密的工作密钥
	 * @param rootKeyUtil 根密钥
	 * @param iv          IV向量
	 * @return 明文的工作密钥
	 */
	public static byte[] decryptWorkKey2Byte(byte[] enWorkKey, RootKeyUtil rootKeyUtil, byte[] iv) {
		return AesCbc.decrypt(enWorkKey, rootKeyUtil.getRootKey(), iv);
	}

	public static byte[] decryptWorkKey2Byte(byte[] enWorkKey, byte[] rootKeyUtil, byte[] iv) {
		return AesCbc.decrypt(enWorkKey, rootKeyUtil, iv);
	}

	/**
	 * 解密 加密的工作密钥
	 *
	 * @param enWorkKey   加密的工作密钥
	 * @param rootKeyUtil 根密钥
	 * @param iv          IV向量
	 * @return 明文的工作密钥
	 */

	public static byte[] decryptWorkKey2ByteGcm(byte[] enWorkKey, RootKeyUtil rootKeyUtil, byte[] iv) {
		return AesGcm.decrypt(enWorkKey, rootKeyUtil.getRootKey(), iv);
	}


	public static byte[] decryptWorkKey2ByteGcm(byte[] enWorkKey, byte[] rootKeyUtil, byte[] iv) {
		return AesGcm.decrypt(enWorkKey, rootKeyUtil, iv);
	}
}
