package com.yyxnb.android.secure.encrypt.keystore.rsa;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.yyxnb.android.secure.utils.LogUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.MGF1ParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

/**
 * 使用keyStore进行RSA加解密
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/26
 */
public abstract class RSAEncryptKS {
	private static final String TAG = RSAEncryptKS.class.getSimpleName();

	private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

	private static final String RSA_MODE_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

	private static final String EMPTY = "";

	private static final int KEY_LENGTH = 2048;
	private static final int KEY_LENGTH_NEW = 3072;

	/**
	 * RSA加密函数 , 2048 bit 密钥 , 标记为Deprecated
	 *
	 * @param alias     keystore的别名
	 * @param plaintext 待加密内容
	 * @return 加密结果 （经过base64编码）
	 */
	@Deprecated
	public static String encrypt(String alias, String plaintext) {
		if (!TextUtils.isEmpty(plaintext)) {
			try {
				return Base64.encodeToString(encrypt(alias, plaintext.getBytes("UTF-8")), Base64.DEFAULT);
			} catch (UnsupportedEncodingException e) {
				LogUtil.e(TAG, "UnsupportedEncodingException: " + e.getMessage());
			}
		}
		return EMPTY;
	}

	/**
	 * RSA解密函数 , 2048 bit 密钥 , 标记为Deprecated
	 *
	 * @param alias     keystore别名
	 * @param encrypted 待解密内容
	 * @return 解密结果 string
	 */
	@Deprecated
	public static String decrpyt(String alias, String encrypted) {
		try {
			return new String(decrpyt(alias, Base64.decode(encrypted, Base64.DEFAULT)), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "UnsupportedEncodingException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception: " + e.getMessage());
		}
		return EMPTY;
	}


	/**
	 * RSA 加密方法, 2048 bit 密钥 , 标记为Deprecated
	 *
	 * @param alias     keystore别名
	 * @param plaintext 待加密明文
	 * @return 加密结果
	 */
	@Deprecated
	public static byte[] encrypt(String alias, byte[] plaintext) {
		return encrypt(alias, plaintext, false);
	}

	/**
	 * RSA 解密方法, 2048 bit 密钥 , 标记为Deprecated
	 *
	 * @param alias     keystore别名
	 * @param encrypted 待解密内容
	 * @return 解密结果
	 */
	@Deprecated
	public static byte[] decrpyt(String alias, byte[] encrypted) {
		byte[] result = new byte[0];
		if (TextUtils.isEmpty(alias) || encrypted == null) {
			LogUtil.e(TAG, "alias or encrypted content is null");
			return result;
		}
		if (!isBuildVersionHigherThan22()) {
			LogUtil.e(TAG, "sdk version is too low");
			return result;
		}

		PrivateKey privateKey = getPrivateKey(alias);
		if (privateKey == null) {
			LogUtil.e(TAG, "Private key is null");
			return result;
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(RSA_MODE_OAEP);
			OAEPParameterSpec sp =
					new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
			cipher.init(Cipher.DECRYPT_MODE, privateKey, sp);
			return cipher.doFinal(encrypted);
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
		return result;
	}

	/**
	 * RSA加密函数
	 *
	 * @param alias     keystore的别名
	 * @param plaintext 待加密内容
	 * @return 加密结果 （经过base64编码）
	 */
	public static String encryptNew(String alias, String plaintext) {
		if (!TextUtils.isEmpty(plaintext)) {
			try {
				return Base64.encodeToString(encryptNew(alias, plaintext.getBytes("UTF-8")), Base64.DEFAULT);
			} catch (UnsupportedEncodingException e) {
				LogUtil.e(TAG, "UnsupportedEncodingException: " + e.getMessage());
			}
		}
		return EMPTY;
	}

	/**
	 * RSA解密函数
	 *
	 * @param alias     keystore别名
	 * @param encrypted 待解密内容
	 * @return 解密结果 string
	 */
	public static String decrpytNew(String alias, String encrypted) {
		try {
			return new String(decrpytNew(alias, Base64.decode(encrypted, Base64.DEFAULT)), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "UnsupportedEncodingException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception: " + e.getMessage());
		}
		return EMPTY;
	}

	/**
	 * RSA 加密方法
	 *
	 * @param alias     keystore别名
	 * @param plaintext 待加密明文
	 * @return 加密结果
	 */
	public static byte[] encryptNew(String alias, byte[] plaintext) {
		return encrypt(alias, plaintext, true);
	}

	/**
	 * RSA 解密方法
	 *
	 * @param alias     keystore别名
	 * @param encrypted 待解密内容
	 * @return 解密结果
	 */
	public static byte[] decrpytNew(String alias, byte[] encrypted) {
		return decrpyt(alias, encrypted);
	}


	private static byte[] encrypt(String alias, byte[] plaintext, boolean is3072) {
		byte[] result = new byte[0];
		if (TextUtils.isEmpty(alias) || plaintext == null) {
			LogUtil.e(TAG, "alias or content is null");
			return result;
		}
		if (!isBuildVersionHigherThan22()) {
			LogUtil.e(TAG, "sdk version is too low");
			return result;
		}

		PublicKey publicKey = getPublicKey(alias, is3072);
		if (publicKey == null) {
			LogUtil.e(TAG, "Public key is null");
			return result;
		}
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(RSA_MODE_OAEP);
			// 不加下面sp变量会抛出IllegalBlockSizeException异常异常，参考：https://issuetracker.google.com/issues/36708951#comment15，https://stackoverflow.com/questions/46042127/android-8-0-illegalblocksizeexception-when-using-rsa-ecb-oaepwithsha-512andmgf1
			OAEPParameterSpec sp =
					new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey, sp);
			return cipher.doFinal(plaintext);
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
		return result;
	}


	/**
	 * 生成RSA公私钥对
	 *
	 * @param alias keystore别名
	 * @return 公私钥对
	 */
	@RequiresApi(api = Build.VERSION_CODES.M)
	private synchronized static KeyPair generateKeyPair(String alias, boolean is3072) {
		if (keyPairExists(alias)) {
			LogUtil.e(TAG, "Key pair exits");
			return null;
		}
		KeyPairGenerator keyPairGenerator;
		KeyPair keyPair = null;

		LogUtil.i(TAG, "generate key pair.");
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
			if (!is3072) {
				keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_DECRYPT)
						.setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
						.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
						.setKeySize(KEY_LENGTH)
						.build());
			} else {
				keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_DECRYPT)
						.setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
						.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
						.setKeySize(KEY_LENGTH_NEW)
						.build());
			}
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (NoSuchProviderException e) {
			LogUtil.e(TAG, "NoSuchProviderException: " + e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			LogUtil.e(TAG, "InvalidAlgorithmParameterException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception: " + e.getMessage());
		}
		return keyPair;
	}

	/**
	 * 获取公钥用于加密
	 *
	 * @param alias 别名
	 * @return 公钥
	 */
	@RequiresApi(api = Build.VERSION_CODES.M)
	private static PublicKey getPublicKey(String alias, boolean is3072) {
		if (!keyPairExists(alias)) {
			generateKeyPair(alias, is3072);
		}
		Certificate certificate = loadCertificate(alias);
		if (certificate != null) {
			return certificate.getPublicKey();
		} else {
			return null;
		}
	}

	/**
	 * 加载公钥证书
	 *
	 * @param alias keystore别名
	 * @return 公钥证书
	 */
	private static Certificate loadCertificate(String alias) {
		Certificate certificate = null;

		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
			keyStore.load(null);
			certificate = keyStore.getCertificate(alias);
		} catch (KeyStoreException e) {
			LogUtil.e(TAG, "KeyStoreException: " + e.getMessage());
		} catch (CertificateException e) {
			LogUtil.e(TAG, "CertificateException: " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (IOException e) {
			LogUtil.e(TAG, "IOException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception: " + e.getMessage());
		}
		return certificate;
	}

	/**
	 * 获取私钥用于解密
	 *
	 * @param alias keystore别名
	 * @return 私钥
	 */
	private static PrivateKey getPrivateKey(String alias) {
		if (!keyPairExists(alias)) {
			return null;
		}
		PrivateKey privateKey = null;

		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
			keyStore.load(null);
			privateKey = (PrivateKey) keyStore.getKey(alias, null);
		} catch (KeyStoreException e) {
			LogUtil.e(TAG, "KeyStoreException: " + e.getMessage());
		} catch (CertificateException e) {
			LogUtil.e(TAG, "CertificateException: " + e.getMessage());
		} catch (UnrecoverableKeyException e) {
			LogUtil.e(TAG, "UnrecoverableKeyException: " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (IOException e) {
			LogUtil.e(TAG, "IOException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception: " + e.getMessage());
		}
		return privateKey;
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
	 * 判断是否存在keyPair
	 *
	 * @param alias
	 * @return
	 */
	private static boolean keyPairExists(String alias) {

		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
			keyStore.load(null);
			return (keyStore.getKey(alias, null) != null);
		} catch (KeyStoreException e) {
			LogUtil.e(TAG, "KeyStoreException: " + e.getMessage());
		} catch (CertificateException e) {
			LogUtil.e(TAG, "CertificateException: " + e.getMessage());
		} catch (UnrecoverableKeyException e) {
			LogUtil.e(TAG, "UnrecoverableKeyException: " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (IOException e) {
			LogUtil.e(TAG, "IOException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception: " + e.getMessage());
		}
		return false;
	}
}
