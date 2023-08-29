package com.yyxnb.android.secure.encrypt.aes;

import android.text.TextUtils;

import com.yyxnb.android.secure.utils.EncryptUtil;
import com.yyxnb.android.secure.utils.HexUtil;
import com.yyxnb.android.secure.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES/CBC加解密算法类
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/26
 */
public final class AesCbc {

	private static final String KEY_HEAD = "security:";

	private static final String AES_CBC_ALGORITHM = "AES/CBC/PKCS5Padding";

	private static final String AES_ALGORITHM = "AES";

	private static final String TAG = "CBC";

	private static final String EMPTY = "";

	private static final int AES_128_CBC_KEY_LEN = 16;

	private static final int AES_128_CBC_IV_LEN = 16;

	/**
	 * 默认构造函数
	 */
	private AesCbc() {

	}

	/**
	 * AES加密
	 *
	 * @param content 待加密内容
	 * @param key     十六进制字符串形式的密钥
	 * @return IV和加密结果混合拼接 string
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

		byte[] secretkey = HexUtil.hexStr2ByteArray(key);
		if (secretkey.length < AES_128_CBC_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 1 key error: 1 key length less than 16 bytes.");
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

		byte[] secretkey = HexUtil.hexStr2ByteArray(key);
		if (secretkey.length < AES_128_CBC_KEY_LEN) {
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
		if (key.length < AES_128_CBC_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 2 key error: 2 key length less than 16 bytes.");
			return EMPTY;
		}

		byte[] ivParameter = EncryptUtil.generateSecureRandom(AES_128_CBC_IV_LEN);
		byte[] encryptResult = encryptNotHex(content, key, ivParameter);
		if (encryptResult == null || encryptResult.length == 0) {
			return EMPTY;
		}
		String hexIv = HexUtil.byteArray2HexStr(ivParameter);
		String hexEncrypted = HexUtil.byteArray2HexStr(encryptResult);
		return mixIvAndEncryWord(hexIv, hexEncrypted);
	}

	/**
	 * AES解密
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
		if (key.length < AES_128_CBC_KEY_LEN) {
			LogUtil.e(TAG, "decrypt 2 key error: 2 key length less than 16 bytes.");
			return EMPTY;
		}

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
		return decrypt(encrypedWord, key, HexUtil.hexStr2ByteArray(ivParameter));
	}

	/**
	 * AES加密
	 *
	 * @param content     待加密内容
	 * @param key         十六进制字符串形式的密钥
	 * @param ivParameter 十六进制字符串形式的IV向量
	 * @return 加密结果 string
	 */
	public static String encrypt(String content, String key, String ivParameter) {

		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "encrypt 3 content is null");
			return EMPTY;
		}
		if (TextUtils.isEmpty(key)) {
			LogUtil.e(TAG, "encrypt 3 key is null");
			return EMPTY;
		}
		if (TextUtils.isEmpty(ivParameter)) {
			LogUtil.e(TAG, "encrypt 3 iv is null");
			return EMPTY;
		}

		byte[] secretkey = HexUtil.hexStr2ByteArray(key);
		byte[] ivParameterBytes = HexUtil.hexStr2ByteArray(ivParameter);
		if (secretkey.length < AES_128_CBC_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 3 key error: 3 key length less than 16 bytes.");
			return EMPTY;
		}

		if (ivParameterBytes.length < AES_128_CBC_IV_LEN) {
			LogUtil.e(TAG, "encrypt 3 iv error: 3 iv length less than 16 bytes.");
			return EMPTY;
		}

		return encrypt(content, secretkey, ivParameterBytes);
	}

	/**
	 * AES解密
	 *
	 * @param content     待解密内容
	 * @param key         十六进制字符串形式的密钥
	 * @param ivParameter 十六进制字符串形式的IV向量
	 * @return 解密结果 string
	 */
	public static String decrypt(String content, String key, String ivParameter) {
		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "decrypt 3 content is null");
			return EMPTY;
		}
		if (TextUtils.isEmpty(key)) {
			LogUtil.e(TAG, "decrypt 3 key is null");
			return EMPTY;
		}
		if (TextUtils.isEmpty(ivParameter)) {
			LogUtil.e(TAG, "decrypt 3 iv is null");
			return EMPTY;
		}

		byte[] secretkey = HexUtil.hexStr2ByteArray(key);
		byte[] ivParameterBytes = HexUtil.hexStr2ByteArray(ivParameter);

		if (secretkey.length < AES_128_CBC_KEY_LEN) {
			LogUtil.e(TAG, "decrypt 3 key error: 3 key length less than 16 bytes.");
			return EMPTY;
		}

		if (ivParameterBytes.length < AES_128_CBC_IV_LEN) {
			LogUtil.e(TAG, "decrypt 3 iv error: 3 iv length less than 16 bytes.");
			return EMPTY;
		}

		return decrypt(content, secretkey, ivParameterBytes);
	}

	/**
	 * AES加密
	 *
	 * @param content     待加密内容
	 * @param key         字节数组形式密钥
	 * @param ivParameter 字节数组形式IV向量
	 * @return 加密结果 string
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
		if (key.length < AES_128_CBC_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 4 key error: 4 key length less than 16 bytes.");
			return EMPTY;
		}

		if (ivParameter == null) {
			LogUtil.e(TAG, "encrypt 4 iv is null");
			return EMPTY;
		}
		if (ivParameter.length < AES_128_CBC_IV_LEN) {
			LogUtil.e(TAG, "encrypt 4 iv error: 4 iv length less than 16 bytes.");
			return EMPTY;
		}
		return HexUtil.byteArray2HexStr(encryptNotHex(content, key, ivParameter));
	}

	/**
	 * AES加密
	 *
	 * @param content     待加密内容
	 * @param key         字节数组形式密钥
	 * @param ivParameter 字节数组形式IV向量
	 * @return 加密结果
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
		if (key.length < AES_128_CBC_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 5 key error: 5 key length less than 16 bytes.");
			return new byte[0];
		}

		if (ivParameter == null) {
			LogUtil.e(TAG, "encrypt 5 iv is null");
			return new byte[0];
		}
		if (ivParameter.length < AES_128_CBC_IV_LEN) {
			LogUtil.e(TAG, "encrypt 5 iv error: 5 iv length less than 16 bytes.");
			return new byte[0];
		}

		try {
			return encrypt(content.getBytes("UTF-8"), key, ivParameter);
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, " cbc encrypt data error" + e.getMessage());
		}
		return new byte[0];
	}

	/**
	 * AES解密
	 *
	 * @param content     待解密内容
	 * @param key         字节数组形式密钥
	 * @param ivParameter 字节数组形式IV向量
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
		if (key.length < AES_128_CBC_KEY_LEN) {
			LogUtil.e(TAG, "decrypt 4 key error: 4 key length less than 16 bytes.");
			return EMPTY;
		}

		if (ivParameter == null) {
			LogUtil.e(TAG, "decrypt 4 iv is null");
			return EMPTY;
		}
		if (ivParameter.length < AES_128_CBC_IV_LEN) {
			LogUtil.e(TAG, "decrypt 4 iv error: 4 iv length less than 16 bytes.");
			return EMPTY;
		}

		try {
			byte[] decrypted = decrypt(HexUtil.hexStr2ByteArray(content), key, ivParameter);
			return new String(decrypted, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, " cbc decrypt data error" + e.getMessage());
		}
		return EMPTY;
	}

	/**
	 * 密文（1-3）+IV（1-3）+密文（4-5）+IV(4-8)+密文（6-8）+IV(9-16)+密文（剩余部分）
	 * 即分别在密文的3、5、8位分别插入对应长度的IV片段。
	 *
	 * @param iv
	 * @param cryptWord
	 * @return
	 */
	private static String mixIvAndEncryWord(String iv, String cryptWord) {
		if (TextUtils.isEmpty(iv) || TextUtils.isEmpty(cryptWord)) {
			return EMPTY;
		}
		try {
			StringBuilder buffer = new StringBuilder();
			buffer.append(cryptWord.substring(0, 6)); // 密文（1-3）,共3个字节，共6个Hex字符
			buffer.append(iv.substring(0, 6)); // IV（1-3）,共3个字节，共6个Hex字符

			buffer.append(cryptWord.substring(6, 10)); // 密文（4-5）
			buffer.append(iv.substring(6, 16)); // IV(4-8)

			buffer.append(cryptWord.substring(10, 16)); // 密文（6-8）
			buffer.append(iv.substring(16)); // IV(9-16)

			buffer.append(cryptWord.substring(16)); // 密文（剩余部分）
			return buffer.toString();
		} catch (Exception e) {
			LogUtil.e(TAG, "mix exception: " + e.getMessage());
		}
		return EMPTY;
	}

	/**
	 * 加密接口
	 *
	 * @param content     待加密数据
	 * @param key         密钥
	 * @param ivParameter iv
	 * @return 加密结果
	 */
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

		if (key.length < AES_128_CBC_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 6 key error: 6 key length less than 16 bytes.");
			return new byte[0];
		}

		if (ivParameter == null) {
			LogUtil.e(TAG, "encrypt 6 iv is null");
			return new byte[0];
		}
		if (ivParameter.length < AES_128_CBC_KEY_LEN) {
			LogUtil.e(TAG, "encrypt 6 iv error: 6 iv length less than 16 bytes.");
			return new byte[0];
		}

		SecretKeySpec secretkey = new SecretKeySpec(key, AES_ALGORITHM);
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
			IvParameterSpec ivparam = new IvParameterSpec(ivParameter);
			cipher.init(Cipher.ENCRYPT_MODE, secretkey, ivparam);
			return cipher.doFinal(content);
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (NoSuchPaddingException e) {
			LogUtil.e(TAG, "NoSuchPaddingException: " + e.getMessage());
		} catch (InvalidKeyException e) {
			LogUtil.e(TAG, "InvalidKeyException: " + e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			LogUtil.e(TAG, "InvalidAlgorithmParameterException: " + e.getMessage());
		} catch (IllegalBlockSizeException e) {
			LogUtil.e(TAG, "IllegalBlockSizeException: " + e.getMessage());
		} catch (BadPaddingException e) {
			LogUtil.e(TAG, "BadPaddingException: " + e.getMessage());
		} catch (NullPointerException e) {
			LogUtil.e(TAG, "NullPointerException: " + e.getMessage());
		}
		return new byte[0];
	}

	/**
	 * 解密接口
	 *
	 * @param content     待解密数据
	 * @param key         密钥
	 * @param ivParameter iv
	 * @return 解密结果
	 */
	public static byte[] decrypt(byte[] content, byte[] key, byte[] ivParameter) {

		if (content == null) {
			LogUtil.e(TAG, "decrypt 6 content is null");
			return new byte[0];
		}
		if (content.length == 0) {
			LogUtil.e(TAG, "decrypt 6 content length is 0");
			return new byte[0];
		}

		if (key == null) {
			LogUtil.e(TAG, "decrypt 6 key is null");
			return new byte[0];
		}

		if (key.length < AES_128_CBC_KEY_LEN) {
			LogUtil.e(TAG, "decrypt 6 key error: 6 key length less than 16 bytes.");
			return new byte[0];
		}

		if (ivParameter == null) {
			LogUtil.e(TAG, "decrypt 6 iv is null");
			return new byte[0];
		}
		if (ivParameter.length < AES_128_CBC_IV_LEN) {
			LogUtil.e(TAG, "decrypt 6 iv error: 6 iv length less than 16 bytes.");
			return new byte[0];
		}
		SecretKeySpec secretkey = new SecretKeySpec(key, AES_ALGORITHM);
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
			IvParameterSpec ivparam = new IvParameterSpec(ivParameter);
			cipher.init(Cipher.DECRYPT_MODE, secretkey, ivparam);
			return cipher.doFinal(content);
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (NoSuchPaddingException e) {
			LogUtil.e(TAG, "NoSuchPaddingException: " + e.getMessage());
		} catch (InvalidKeyException e) {
			LogUtil.e(TAG, "InvalidKeyException: " + e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			LogUtil.e(TAG, "InvalidAlgorithmParameterException: " + e.getMessage());
		} catch (IllegalBlockSizeException e) {
			LogUtil.e(TAG, "IllegalBlockSizeException: " + e.getMessage());
		} catch (BadPaddingException e) {
			LogUtil.e(TAG, "BadPaddingException: " + e.getMessage());
			LogUtil.e(TAG, "key is not right");
		} catch (NullPointerException e) {
			LogUtil.e(TAG, "NullPointerException: " + e.getMessage());
		}
		return new byte[0];
	}

	/**
	 * 加密接口
	 *
	 * @param content 待加密数据
	 * @param key     密钥
	 * @return 加密结果
	 */
	public static byte[] encrypt(byte[] content, byte[] key) {
		byte[] ivParameter = EncryptUtil.generateSecureRandom(AES_128_CBC_IV_LEN);
		byte[] encryptContent = encrypt(content, key, ivParameter);
		return byteMerger(ivParameter, encryptContent);
	}

	/**
	 * 解密接口
	 *
	 * @param content 待解密数据
	 * @param key     密钥
	 * @return 解密结果
	 */
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
		byte[] iv = new byte[AES_128_CBC_IV_LEN];
		System.arraycopy(bt, 0, iv, 0, AES_128_CBC_IV_LEN);
		return iv;
	}

	private static byte[] getEncryptContent(byte[] bt) {
		byte[] encryptContent = new byte[bt.length - AES_128_CBC_IV_LEN];
		System.arraycopy(bt, AES_128_CBC_IV_LEN, encryptContent, 0, bt.length - AES_128_CBC_IV_LEN);
		return encryptContent;
	}

	/**
	 * 获取iv。 密文（1-3）+IV（1-3）+密文（4-5）+IV(4-8)+密文（6-8）+IV(9-16)+密文（剩余部分）
	 * 即分别在密文的3、5、8位分别插入对应长度的IV片段。
	 *
	 * @param src
	 * @return
	 */
	private static String getIv(String src) {
		if (TextUtils.isEmpty(src)) {
			return EMPTY;
		}
		try {
			StringBuilder iv = new StringBuilder();
			iv.append(src.substring(6, 12));
			iv.append(src.substring(16, 26));
			iv.append(src.substring(32, 48));
			return iv.toString();
		} catch (Exception e) {
			LogUtil.e(TAG, "getIv exception : " + e.getMessage());
		}
		return EMPTY;
	}

	/**
	 * 获取密文 密文（1-3）+IV（1-3）+密文（4-5）+IV(4-8)+密文（6-8）+IV(9-16)+密文（剩余部分）
	 * 即分别在密文的3、5、8位分别插入对应长度的IV片段。
	 *
	 * @param src
	 * @return
	 */
	private static String getEncryptWord(String src) {
		if (TextUtils.isEmpty(src)) {
			return EMPTY;
		}
		try {
			StringBuilder word = new StringBuilder();
			word.append(src.substring(0, 6));
			word.append(src.substring(12, 16));
			word.append(src.substring(26, 32));
			word.append(src.substring(48));
			return word.toString();
		} catch (Exception e) {
			LogUtil.e(TAG, "get encryptword exception : " + e.getMessage());
		}
		return EMPTY;
	}

	/**
	 * 脱去AES密文的"security:";头
	 *
	 * @param encryptText
	 * @return
	 */
	static String stripCryptHead(String encryptText) {
		if (TextUtils.isEmpty(encryptText)) {
			return EMPTY;
		}
		int index = encryptText.indexOf(KEY_HEAD);
		return index == -1 ? EMPTY : encryptText.substring(KEY_HEAD.length());
	}

	/**
	 * 剥去头部，兼容服务端
	 *
	 * @param encryptText
	 * @return
	 */
	static byte[] stripCryptHead(byte[] encryptText) {

		String encryptStr = "";
		try {
			encryptStr = new String(encryptText, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "stripCryptHead: exception : " + e.getMessage());
		}

		if (!encryptStr.startsWith(KEY_HEAD)) {
			return new byte[0];
		}
		if (encryptText.length > KEY_HEAD.length()) {
			byte[] newStr = new byte[encryptText.length - KEY_HEAD.length()];
			System.arraycopy(encryptText, KEY_HEAD.length(), newStr, 0, newStr.length);
			return newStr;
		} else {
			return new byte[0];
		}
	}

	/**
	 * AES_CBC模式解密带有"security:"头的密文，密文为String
	 *
	 * @param content
	 * @param key
	 * @return
	 */
	public static String decryptWithCryptHead(String content, byte[] key) {
		if (TextUtils.isEmpty(content) || key == null || key.length < AES_128_CBC_KEY_LEN) {
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
			LogUtil.e(TAG, " cbc cipherText data missing colon");
			return EMPTY;
		}

		return decrypt(HexUtil.byteArray2HexStr(cipherText), key, iv);
	}

	/**
	 * AES_CBC模式解密带有"security:"头的密文，密文为byte数组
	 * 云测字节数组类型的content加密类型为"security:".getBytes() + iv字节数组 + ":".getBytes()+
	 * cipherText字节数组
	 *
	 * @param content
	 * @param key
	 * @return
	 */
	public static String decryptWithCryptHead(byte[] content, byte[] key) {
		try {
			return new String(decryptWithCryptHeadReturnByte(content, key), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "decryptWithCryptHead UnsupportedEncodingException ");
		}
		return "";
	}

	public static byte[] decryptWithCryptHeadReturnByte(byte[] content, byte[] key) {
		if (content == null || key == null || key.length < AES_128_CBC_KEY_LEN) {
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
			LogUtil.e(TAG, " cbc cipherText data missing colon");
			return new byte[0];
		}
		return decrypt(cipherText, key, iv);
	}

	private static int findByteIndexFromIv(byte[] ivText) {
		if (ivText[AES_128_CBC_IV_LEN] == ':') {
			return AES_128_CBC_IV_LEN;
		} else {
			return -1;
		}
	}
}
