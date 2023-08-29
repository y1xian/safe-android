package com.yyxnb.android.secure.encrypt.aes;

import android.os.Build;
import android.text.TextUtils;

import com.yyxnb.android.secure.utils.EncryptUtil;
import com.yyxnb.android.secure.utils.HexUtil;
import com.yyxnb.android.secure.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES/GCM加解密算法类
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/26
 */
public final class AesGcm {

	private static final String KEY_HEAD = "security:";

	private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";

	private static final String AES_ALGORITHM = "AES";

	private static final String TAG = "GCM";

	private static final String EMPTY = "";

	private static final int AES_GCM_KEY_LEN = 16;

	private static final int AES_GCM_IV_LEN = 12;

	private static final int TIMES = 2;

	/**
	 * 默认构造函数
	 */
	private AesGcm() {

	}

	/**
	 * AES GCM加密
	 *
	 * @param content 待加密内容
	 * @param key     十六进制字符串形式的密钥
	 * @return IV和加密结果拼接 string
	 */

	public static String encrypt(String content, String key) {
		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "encrypt 1 content is null");
			return EMPTY;
		}
		if (TextUtils.isEmpty(key)) {
			LogUtil.e(TAG, "encrypt 1 key is null");
			return EMPTY;
		}


		if (!isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "encrypt 1 build version not higher than 19");
			return EMPTY;
		}

		byte[] secretkey = HexUtil.hexStr2ByteArray(key);
		if (secretkey.length < AES_GCM_KEY_LEN) {
			LogUtil.e(TAG, "encrypt key error: key length less than 16 bytes.");
			return EMPTY;
		}
		return encrypt(content, secretkey);
	}

	/**
	 * AES解密
	 *
	 * @param content 待解密内容
	 * @param key     十六进制字符串形式的密钥
	 * @return 解密结果 string
	 */

	public static String decrypt(String content, String key) {

		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "decrypt 1 content is null");
			return EMPTY;
		}
		if (TextUtils.isEmpty(key)) {
			LogUtil.e(TAG, "decrypt 1 key is null");
			return EMPTY;
		}

		if (!isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "decrypt 1 build version not higher than 19");
			return EMPTY;
		}
		byte[] secretkey = HexUtil.hexStr2ByteArray(key);
		if (secretkey.length < AES_GCM_KEY_LEN) {
			LogUtil.e(TAG, "decrypt 1 key error: 1 key length less than 16 bytes.");
			return EMPTY;
		}
		return decrypt(content, secretkey);
	}

	/**
	 * AES加密
	 *
	 * @param content 待加密内容
	 * @param key     字节数组形式密钥
	 * @return IV和加密结果混合拼接 string
	 */

	public static String encrypt(String content, byte[] key) {
		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "encrypt 2 content is null");
			return EMPTY;
		}
		if (key == null) {
			LogUtil.e(TAG, "encrypt 2 key is null");
			return EMPTY;
		}
		if (key.length < AES_GCM_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 2 key error: 2 key length less than 16 bytes.");
			return EMPTY;
		}

		if (!isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "encrypt 2 build version not higher than 19");
			return EMPTY;
		}

		byte[] ivParameter = EncryptUtil.generateSecureRandom(AES_GCM_IV_LEN);
		byte[] encryptResult = encryptNotHex(content, key, ivParameter);
		if (encryptResult == null || encryptResult.length == 0) {
			return EMPTY;
		}
		// 将本次使用的IV向量与密文保存在一起，用于后期解密
		String hexIv = HexUtil.byteArray2HexStr(ivParameter);
		String hexEncrypted = HexUtil.byteArray2HexStr(encryptResult);
		return hexIv + hexEncrypted;
	}

	/**
	 * AES GCM解密，要求系统版本不低于19
	 *
	 * @param content 待解密内容
	 * @param key     字节数组形式密钥
	 * @return 解密结果 string
	 */

	public static String decrypt(String content, byte[] key) {

		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "decrypt 2 content is null");
			return EMPTY;
		}
		if (key == null) {
			LogUtil.e(TAG, "decrypt 2 key is null");
			return EMPTY;
		}
		if (key.length < AES_GCM_KEY_LEN) {
			LogUtil.e(TAG, "decrypt 2 key error: 2 key length less than 16 bytes.");
			return EMPTY;
		}

		if (!isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "decrypt 2 build version not higher than 19");
			return EMPTY;
		}
		try {
			SecretKeySpec secretkey = new SecretKeySpec(key, AES_ALGORITHM);
			Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);

			// 从密文中获取加密使用的IV向量和密文
			String ivParameter = getIv(content);
			String encrypedWord = getEncryptWord(content);

			if (TextUtils.isEmpty(ivParameter)) {
				LogUtil.e(TAG, "decrypt 2 iv is null");
				return EMPTY;
			}

			if (TextUtils.isEmpty(encrypedWord)) {
				LogUtil.e(TAG, "decrypt 2 encrypt content is null");
				return EMPTY;
			}

			AlgorithmParameterSpec algorithmParameterSpec = getGcmAlgorithmParams(HexUtil.hexStr2ByteArray(ivParameter));
			cipher.init(Cipher.DECRYPT_MODE, secretkey, algorithmParameterSpec);
			byte[] decrypted = cipher.doFinal(HexUtil.hexStr2ByteArray(encrypedWord));
			return new String(decrypted, "UTF-8");

		} catch (NullPointerException | GeneralSecurityException | UnsupportedEncodingException e) {
			LogUtil.e(TAG, "GCM decrypt data exception: " + e.getMessage());
		}
		return EMPTY;
	}

	/**
	 * AES GCM加密
	 *
	 * @param content 待加密内容
	 * @param key     十六进制字符串形式的密钥
	 * @param iv      十六进制字符串形式的IV
	 * @return 十六进制编码后的加密结果
	 */

	public static String encrypt(String content, String key, String iv) {

		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "encrypt 3 content is null");
			return EMPTY;
		}
		if (TextUtils.isEmpty(key)) {
			LogUtil.e(TAG, "encrypt 3 key is null");
			return EMPTY;
		}
		if (TextUtils.isEmpty(iv)) {
			LogUtil.e(TAG, "encrypt 3 iv is null");
			return EMPTY;
		}

		if (!isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "encrypt 3 build version not higher than 19");
			return EMPTY;
		}
		byte[] secretkey = HexUtil.hexStr2ByteArray(key);
		byte[] ivParameter = HexUtil.hexStr2ByteArray(iv);

		if (secretkey.length < AES_GCM_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 3 key error: 3 key length less than 16 bytes.");
			return EMPTY;
		}

		if (ivParameter.length < AES_GCM_IV_LEN) {
			LogUtil.e(TAG, "encrypt 3 iv error: 3 iv length less than 16 bytes.");
			return EMPTY;
		}
		return encrypt(content, secretkey, ivParameter);
	}

	/**
	 * AES解密
	 *
	 * @param content 待解密内容
	 * @param key     十六进制字符串形式的密钥
	 * @param iv      十六进制字符串形式的iv
	 * @return 解密结果 string
	 */

	public static String decrypt(String content, String key, String iv) {

		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "decrypt 3 content is null");
			return EMPTY;
		}
		if (TextUtils.isEmpty(key)) {
			LogUtil.e(TAG, "decrypt 3 key is null");
			return EMPTY;
		}
		if (TextUtils.isEmpty(iv)) {
			LogUtil.e(TAG, "decrypt 3 iv is null");
			return EMPTY;
		}

		if (!isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "decrypt 3 build version not higher than 19");
			return EMPTY;
		}
		byte[] secretkey = HexUtil.hexStr2ByteArray(key);
		byte[] ivParameter = HexUtil.hexStr2ByteArray(iv);

		if (secretkey.length < AES_GCM_KEY_LEN) {
			LogUtil.e(TAG, "decrypt 3 key error: 3 key length less than 16 bytes.");
			return EMPTY;
		}

		if (ivParameter.length < AES_GCM_IV_LEN) {
			LogUtil.e(TAG, "decrypt 3 iv error: 3 iv length less than 16 bytes.");
			return EMPTY;
		}

		return decrypt(content, secretkey, ivParameter);
	}

	/**
	 * AES加密
	 *
	 * @param content     待加密内容
	 * @param key         字节数组形式密钥
	 * @param ivParameter 字节数组形式iv
	 * @return 十六进制编码后的加密结果
	 */

	public static String encrypt(String content, byte[] key, byte[] ivParameter) {

		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "encrypt 4 content is null");
			return EMPTY;
		}
		if (key == null) {
			LogUtil.e(TAG, "encrypt 4 key is null");
			return EMPTY;
		}
		if (key.length < AES_GCM_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 4 key error: 3 key length less than 16 bytes.");
			return EMPTY;
		}

		if (ivParameter == null) {
			LogUtil.e(TAG, "encrypt 4 iv is null");
			return EMPTY;
		}
		if (ivParameter.length < AES_GCM_IV_LEN) {
			LogUtil.e(TAG, "encrypt 3 iv error: 3 iv length less than 16 bytes.");
			return EMPTY;
		}

		if (!isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "encrypt 4 build version not higher than 19");
			return EMPTY;
		}
		return HexUtil.byteArray2HexStr(encryptNotHex(content, key, ivParameter));
	}

	/**
	 * AES GCM解密，要求系统版本不低于19
	 *
	 * @param content     待解密内容
	 * @param key         字节数组形式密钥
	 * @param ivParameter 字节数组形式iv
	 * @return 解密结果 string
	 */

	public static String decrypt(String content, byte[] key, byte[] ivParameter) {
		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "decrypt 4 content is null");
			return EMPTY;
		}
		if (key == null) {
			LogUtil.e(TAG, "decrypt 4 key is null");
			return EMPTY;
		}
		if (key.length < AES_GCM_KEY_LEN) {
			LogUtil.e(TAG, "decrypt 4 key error: 4 key length less than 16 bytes.");
			return EMPTY;
		}

		if (ivParameter == null) {
			LogUtil.e(TAG, "decrypt 4 iv is null");
			return EMPTY;
		}
		if (ivParameter.length < AES_GCM_IV_LEN) {
			LogUtil.e(TAG, "decrypt 4 iv error: 4 iv length less than 16 bytes.");
			return EMPTY;
		}
		if (!isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "decrypt 4 build version not higher than 19");
			return EMPTY;
		}

		try {
			byte[] encryptContent = HexUtil.hexStr2ByteArray(content);
			byte[] decryptContent = decrypt(encryptContent, key, ivParameter);
			return new String(decryptContent, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "GCM decrypt data exception: " + e.getMessage());
		}
		return EMPTY;
	}

	/**
	 * AES加密
	 *
	 * @param content     待加密内容
	 * @param key         字节数组形式密钥
	 * @param ivParameter 字节数组形式iv
	 * @return 十六进制编码后的加密结果
	 */

	private static byte[] encryptNotHex(String content, byte[] key, byte[] ivParameter) {

		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "encrypt 5 content is null");
			return new byte[0];
		}
		if (key == null) {
			LogUtil.e(TAG, "encrypt 5 key is null");
			return new byte[0];
		}
		if (key.length < AES_GCM_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 5 key error: 5 key length less than 16 bytes.");
			return new byte[0];
		}

		if (ivParameter == null) {
			LogUtil.e(TAG, "encrypt 5 iv is null");
			return new byte[0];
		}
		if (ivParameter.length < AES_GCM_IV_LEN) {
			LogUtil.e(TAG, "encrypt 5 iv error: 5 iv length less than 16 bytes.");
			return new byte[0];
		}

		if (!isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "encrypt 5 build version not higher than 19");
			return new byte[0];
		}
		try {
			return encrypt(content.getBytes("UTF-8"), key, ivParameter);
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "GCM encrypt data error" + e.getMessage());
		}
		return new byte[0];
	}


	public static byte[] encrypt(byte[] content, byte[] key, byte[] ivParameter) {

		if (content == null) {
			LogUtil.e(TAG, "encrypt 6 content is null");
			return new byte[0];
		}
		if (content.length == 0) {
			LogUtil.e(TAG, "encrypt 6 content length is 0");
			return new byte[0];
		}

		if (key == null) {
			LogUtil.e(TAG, "encrypt 6 key is null");
			return new byte[0];
		}

		if (key.length < AES_GCM_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 6 key error: 6 key length less than 16 bytes.");
			return new byte[0];
		}

		if (ivParameter == null) {
			LogUtil.e(TAG, "encrypt 6 iv is null");
			return new byte[0];
		}
		if (ivParameter.length < AES_GCM_IV_LEN) {
			LogUtil.e(TAG, "encrypt 6 iv error: 6 iv length less than 16 bytes.");
			return new byte[0];
		}

		if (!isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "encrypt 6 build version not higher than 19");
			return new byte[0];
		}

		try {
			SecretKeySpec secretkey = new SecretKeySpec(key, AES_ALGORITHM);
			Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
			AlgorithmParameterSpec algorithmParameterSpec = getGcmAlgorithmParams(ivParameter);
			cipher.init(Cipher.ENCRYPT_MODE, secretkey, algorithmParameterSpec);
			return cipher.doFinal(content);
		} catch (GeneralSecurityException e) {
			LogUtil.e(TAG, "GCM encrypt data error" + e.getMessage());
		} catch (NullPointerException e) {
			LogUtil.e(TAG, "GCM encrypt data error" + e.getMessage());
		}
		return new byte[0];
	}


	public static byte[] decrypt(byte[] encryptContent, byte[] key, byte[] ivParameter) {

		if (encryptContent == null) {
			LogUtil.e(TAG, "decrypt 6 content is null");
			return new byte[0];
		}
		if (encryptContent.length == 0) {
			LogUtil.e(TAG, "decrypt 6 content length is 0");
			return new byte[0];
		}

		if (key == null) {
			LogUtil.e(TAG, "decrypt 6 key is null");
			return new byte[0];
		}

		if (key.length < AES_GCM_KEY_LEN) {
			LogUtil.e(TAG, "decrypt 6 key error: 6 key length less than 16 bytes.");
			return new byte[0];
		}

		if (ivParameter == null) {
			LogUtil.e(TAG, "decrypt 6 iv is null");
			return new byte[0];
		}
		if (ivParameter.length < AES_GCM_IV_LEN) {
			LogUtil.e(TAG, "decrypt 6 iv error: 6 iv length less than 16 bytes.");
			return new byte[0];
		}

		if (!isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "decrypt 6 build version not higher than 19");
			return new byte[0];
		}

		try {
			SecretKeySpec secretkey = new SecretKeySpec(key, AES_ALGORITHM);
			Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
			AlgorithmParameterSpec algorithmParameterSpec = getGcmAlgorithmParams(ivParameter);
			cipher.init(Cipher.DECRYPT_MODE, secretkey, algorithmParameterSpec);
			return cipher.doFinal(encryptContent);
		} catch (GeneralSecurityException e) {
			LogUtil.e(TAG, "GCM decrypt data exception: " + e.getMessage());
		}
		return new byte[0];
	}


	public static byte[] encrypt(byte[] content, byte[] key) {
		byte[] ivParameter = EncryptUtil.generateSecureRandom(AES_GCM_IV_LEN);
		byte[] encryptContent = encrypt(content, key, ivParameter);
		return byteMerger(ivParameter, encryptContent);
	}


	public static byte[] decrypt(byte[] content, byte[] key) {
		byte[] ivParameter = getIV(content);
		byte[] encryptContent = getEncryptContent(content);
		return decrypt(encryptContent, key, ivParameter);
	}

	private static byte[] byteMerger(byte[] bt1, byte[] bt2) {
		byte[] bt3 = new byte[bt1.length + bt2.length];
		System.arraycopy(bt1, 0, bt3, 0, bt1.length);
		System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
		return bt3;
	}

	private static byte[] getIV(byte[] bt) {
		byte[] iv = new byte[AES_GCM_IV_LEN];
		System.arraycopy(bt, 0, iv, 0, AES_GCM_IV_LEN);
		return iv;
	}

	private static byte[] getEncryptContent(byte[] bt) {
		byte[] encryptContent = new byte[bt.length - AES_GCM_IV_LEN];
		System.arraycopy(bt, AES_GCM_IV_LEN, encryptContent, 0, bt.length - AES_GCM_IV_LEN);
		return encryptContent;
	}


	/**
	 * 从密文中获取IV
	 *
	 * @param src 密文
	 * @return IV
	 */
	private static String getIv(String src) {
		if (TextUtils.isEmpty(src) || src.length() < AES_GCM_IV_LEN * TIMES) {
			LogUtil.e(TAG, "IV is invalid.");
			return EMPTY;
		}
		return src.substring(0, AES_GCM_IV_LEN * TIMES);
	}

	/**
	 * 从密文中获取去掉IV的密文内容
	 *
	 * @param src 密文
	 * @return 去掉IV后的密文
	 */
	private static String getEncryptWord(String src) {
		if (TextUtils.isEmpty(src) || src.length() < AES_GCM_IV_LEN * TIMES) {
			return EMPTY;
		}
		return src.substring(AES_GCM_IV_LEN * TIMES);
	}

	/**
	 * 判断当前系统版本是否大于等于19
	 *
	 * @return
	 */
	public static boolean isBuildVersionHigherThan19() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	/**
	 * AES_GCM模式解密带有"security:"头的密文，密文为String
	 *
	 * @param content
	 * @param key
	 * @return
	 */

	public static String decryptWithCryptHead(String content, byte[] key) {
		if (TextUtils.isEmpty(content) || key == null || key.length < AES_GCM_KEY_LEN) {
			return EMPTY;
		}
		String subContent = AesCbc.stripCryptHead(content);
		if (EMPTY.equals(subContent)) {
			return EMPTY;
		}
		int index = subContent.indexOf(':');
		byte[] iv = null;
		byte[] cipherText = null;
		if (index >= 0) {
			iv = HexUtil.hexStr2ByteArray(subContent.substring(0, index));
			cipherText = HexUtil.hexStr2ByteArray(subContent.substring(index + 1));
		} else {
			LogUtil.e(TAG, " gcm cipherText data missing colon");
			return EMPTY;
		}
		return decrypt(HexUtil.byteArray2HexStr(cipherText), key, iv);
	}

	/**
	 * AES_GCM模式解密带有"security:"头的密文，密文为byte数组
	 * 云测字节数组类型的content加密类型为"security:".getBytes() + iv字节数组 + ":".getBytes()+ cipherText字节数组
	 *
	 * @param content
	 * @param key
	 * @return string类型的明文
	 */

	public static String decryptWithCryptHead(byte[] content, byte[] key) {
		try {
			return new String(decryptWithCryptHeadReturnByte(content, key), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "UnsupportedEncodingException");
		}
		return "";
	}

	/**
	 * AES_GCM模式解密带有"security:"头的密文，密文为byte数组
	 * 云测字节数组类型的content加密类型为"security:".getBytes() + iv字节数组 + ":".getBytes()+ cipherText字节数组
	 *
	 * @param content
	 * @param key
	 * @return byte 类型的明文
	 */

	public static byte[] decryptWithCryptHeadReturnByte(byte[] content, byte[] key) {
		if (content == null || key == null || key.length < AES_GCM_KEY_LEN) {
			return new byte[0];
		}
		byte[] subContent = AesCbc.stripCryptHead(content);
		if (subContent.length == 0) {
			return new byte[0];
		}
		int index = findByteIndexFromIv(subContent);
		byte[] iv = null;
		byte[] cipherText = null;
		if (index >= 0) {
			iv = Arrays.copyOf(subContent, index);
			int cipherTextLen = subContent.length - iv.length - ":".length();
			cipherText = new byte[cipherTextLen];
			System.arraycopy(subContent, index + 1, cipherText, 0, cipherTextLen);
		} else {
			LogUtil.e(TAG, " gcm cipherText data missing colon");
			return new byte[0];
		}
		return decrypt(cipherText, key, iv);
	}

	private static int findByteIndexFromIv(byte[] ivText) {
		if (ivText[AES_GCM_IV_LEN] == ':') {
			return AES_GCM_IV_LEN;
		} else {
			return -1;
		}
	}


	/**
	 * 19,20的系统不能使用GCMParameterSpec作为参数，否则无法解密
	 *
	 * @param buf
	 * @return
	 */
	public static AlgorithmParameterSpec getGcmAlgorithmParams(final byte[] buf) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			// GCMParameterSpec should always be present in Java 7 or newer, but it's missing on
			// some Android devices with API level <= 19. Fortunately, we can initialize the cipher
			// with just an IvParameterSpec. It will use a tag size of 128 bits.
			return new IvParameterSpec(buf);
		}
		return new GCMParameterSpec(128, buf);
	}
}
