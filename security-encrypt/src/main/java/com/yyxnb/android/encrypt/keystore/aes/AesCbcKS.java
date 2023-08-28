package com.yyxnb.android.encrypt.keystore.aes;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.yyxnb.android.encrypt.utils.HexUtil;
import com.yyxnb.android.encrypt.utils.LogUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * 使用AndroidKeyStore的机制管理密钥，提供AES/CBC加解密算法，
 * API 23 及以上才支持，keystore路径：/data/misc/keystore/user_0/
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/26
 */
public class AesCbcKS {

	private static final String TAG = "CBCKS";

	/**
	 * 固定名称，不可修改
	 */
	private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

	private static final String AES_CBC_ALGORITHM = "AES/CBC/PKCS7Padding";

	private static final String EMPTY = "";

	private static final int AES_CBC_IV_LEN = 16;

	private static final int AES_CBC_KEY_BIT_LEN = 256;

	/*
	将keystore缓存在内存中，提高效率
	 */
	private static Map<String, SecretKey> keyMap = new HashMap<>();

	/**
	 * AES/CBC加密
	 *
	 * @param alias   keystore的别名，注意，不同算法别名不能相同，keystore路径：/data/misc/keystore/user_0/
	 * @param content 待加密的数据
	 * @return 加密后的数据. string
	 */

	public static String encrypt(String alias, String content) {
		if (TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "encrypt 1 content is null");
			return EMPTY;
		}
		try {
			return HexUtil.byteArray2HexStr(encrypt(alias, content.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "encrypt: UnsupportedEncodingException");
		}
		return EMPTY;
	}

	/**
	 * AES解密
	 *
	 * @param alias   keystore的别名
	 * @param content the content
	 * @return 解密后的数据. string
	 */

	public static String decrypt(String alias, String content) {
		if (TextUtils.isEmpty(alias) || TextUtils.isEmpty(content)) {
			LogUtil.e(TAG, "alias or encrypt content is null");
			return EMPTY;
		}
		byte[] data = HexUtil.hexStr2ByteArray(content);
		try {
			return new String(decrypt(alias, data), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "encrypt: UnsupportedEncodingException");
		}
		return EMPTY;
	}

	/**
	 * 创建keystore，版本需要在M（23）及以上
	 *
	 * @param alias keystore别名
	 * @return 返回SecretKey
	 */

	@RequiresApi(api = Build.VERSION_CODES.M)
	private synchronized static SecretKey generateKey(String alias) {
		LogUtil.i(TAG, "load key");
		SecretKey secretKey = null;
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
			keyStore.load(null);
			Key key = keyStore.getKey(alias, null);
			if (key != null && key instanceof SecretKey) {
				secretKey = (SecretKey) key;
			} else {
				LogUtil.i(TAG, "generate key");
				KeyGenerator keyGenerator =
						KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
				keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
						KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
						.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
						.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
						.setKeySize(AES_CBC_KEY_BIT_LEN)
						.build());
				secretKey = keyGenerator.generateKey();
			}
		} catch (KeyStoreException e) {
			LogUtil.e(TAG, "KeyStoreException: " + e.getMessage());
		} catch (IOException e) {
			LogUtil.e(TAG, "IOException: " + e.getMessage());
		} catch (CertificateException e) {
			LogUtil.e(TAG, "CertificateException: " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (UnrecoverableKeyException e) {
			LogUtil.e(TAG, "UnrecoverableKeyException: " + e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			LogUtil.e(TAG, "InvalidAlgorithmParameterException: " + e.getMessage());
		} catch (NoSuchProviderException e) {
			LogUtil.e(TAG, "NoSuchProviderException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception: " + e.getMessage());
		}
		keyMap.put(alias, secretKey);
		return secretKey;
	}

	/**
	 * 加密byte，返回byte
	 *
	 * @param alias   keystore别名
	 * @param content 待加密内容
	 * @return 加密结果，iv在前面
	 */

	public static byte[] encrypt(String alias, byte[] content) {
		byte[] result = new byte[0];
		if (TextUtils.isEmpty(alias) || content == null) {
			LogUtil.e(TAG, "alias or encrypt content is null");
			return result;
		}
		if (!isBuildVersionHigherThan22()) {
			LogUtil.e(TAG, "sdk version is too low");
			return result;
		}

		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
			SecretKey secretKey = getKey(alias);
			if (secretKey == null) {
				LogUtil.e(TAG, "encrypt secret key is null");
				return result;
			}
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptBytes = cipher.doFinal(content);

			byte[] iv = cipher.getIV();
			if (iv == null || iv.length != AES_CBC_IV_LEN) {
				LogUtil.e(TAG, "IV is invalid.");
				return result;
			}
			result = Arrays.copyOf(iv, iv.length + encryptBytes.length);
			System.arraycopy(encryptBytes, 0, result, iv.length, encryptBytes.length);
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (NoSuchPaddingException e) {
			LogUtil.e(TAG, "NoSuchPaddingException: " + e.getMessage());
		} catch (BadPaddingException e) {
			LogUtil.e(TAG, "BadPaddingException: " + e.getMessage());
		} catch (IllegalBlockSizeException e) {
			LogUtil.e(TAG, "IllegalBlockSizeException: " + e.getMessage());
		} catch (InvalidKeyException e) {
			LogUtil.e(TAG, "InvalidKeyException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception: " + e.getMessage());
		}
		return result;
	}

	/**
	 * 解密byte
	 *
	 * @param alias   keystore别名
	 * @param content 待解密内容
	 * @return 解密结果
	 */

	public static byte[] decrypt(String alias, byte[] content) {
		byte[] decryptedData = new byte[0];
		if (TextUtils.isEmpty(alias) || content == null) {
			LogUtil.e(TAG, "alias or encrypt content is null");
			return decryptedData;
		}
		if (!isBuildVersionHigherThan22()) {
			LogUtil.e(TAG, "sdk version is too low");
			return decryptedData;
		}

		if (content.length <= AES_CBC_IV_LEN) {
			LogUtil.e(TAG, "Decrypt source data is invalid.");
			return decryptedData;
		}
		SecretKey secretKey = getKey(alias);
		if (secretKey == null) {
			LogUtil.e(TAG, "decrypt secret key is null");
			return decryptedData;
		}
		byte[] iv = Arrays.copyOf(content, AES_CBC_IV_LEN);
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
			return cipher.doFinal(content, AES_CBC_IV_LEN, content.length - AES_CBC_IV_LEN);
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
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception: " + e.getMessage());
		}
		return decryptedData;
	}

	/**
	 * 判断当前系统版本是否大于等于M
	 *
	 * @return
	 */
	private static boolean isBuildVersionHigherThan22() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	/**
	 * 优先从内存中获取keystore，如果获取不到再load，如果keystore还未创建，再创建keystore
	 *
	 * @param alias
	 * @return
	 */
	@RequiresApi(api = Build.VERSION_CODES.M)
	private static SecretKey getKey(String alias) {
		if (TextUtils.isEmpty(alias)) {
			return null;
		}
		if (keyMap.get(alias) == null) {
			generateKey(alias);
		}
		return keyMap.get(alias);
	}
}