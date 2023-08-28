package com.yyxnb.android.encrypt.keystore.rsa;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.yyxnb.android.encrypt.utils.LogUtil;

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
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * 使用keyStore进行签名和验签
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/26
 */
public abstract class RSASignKS {
	private static final String TAG = RSASignKS.class.getSimpleName();

	private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

	private static final String RSA_MODE_SIGN = "SHA256withRSA/PSS";

	private static final String EMPTY = "";

	private static final int KEY_LENGTH = 2048;

	private static final int KEY_LENGTH_NEW = 3072;

	/**
	 * 采用私钥对数据进行签名，密钥长度2048已不推荐使用，标记为Deprecated
	 *
	 * @param alias 别名
	 * @param data  待签名内容
	 * @return 签名结果 string
	 */
	@Deprecated

	public static String sign(String alias, String data) {
		try {
			return Base64.encodeToString(sign(alias, data.getBytes("UTF-8")), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "sign UnsupportedEncodingException : " + e.getMessage());
		}
		return EMPTY;
	}

	/**
	 * 采用公钥对签名内容验证，密钥长度2048已不推荐使用，标记为Deprecated
	 *
	 * @param alias     别名
	 * @param data      签名前的内容
	 * @param signValue 签名后的结果
	 * @return 验签的结果 boolean
	 */
	@Deprecated

	public static boolean verifySign(String alias, String data, String signValue) {
		try {
			return verifySign(alias, data.getBytes("UTF-8"), Base64.decode(signValue, Base64.DEFAULT));
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "verifySign UnsupportedEncodingException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "base64 decode Exception" + e.getMessage());
		}
		return false;
	}

	/**
	 * 采用私钥对数据进行签名，采用3072 bit的密钥长度
	 *
	 * @param alias 别名
	 * @param data  待签名内容
	 * @return 签名结果 string
	 */

	public static String signNew(String alias, String data) {
		try {
			return Base64.encodeToString(signNew(alias, data.getBytes("UTF-8")), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "sign UnsupportedEncodingException : " + e.getMessage());
		}
		return EMPTY;
	}

	/**
	 * 采用公钥对签名内容验证，采用3072 bit的密钥长度
	 *
	 * @param alias     别名
	 * @param data      签名前的内容
	 * @param signValue 签名后的结果
	 * @return 验签的结果 boolean
	 */

	public static boolean verifySignNew(String alias, String data, String signValue) {
		try {
			return verifySignNew(alias, data.getBytes("UTF-8"), Base64.decode(signValue, Base64.DEFAULT));
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "verifySign UnsupportedEncodingException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "base64 decode Exception" + e.getMessage());
		}
		return false;
	}

	/**
	 * 签名byte[] 数据，2048 bit 密钥，已不推荐使用，标记为Deprecated
	 *
	 * @param alias keystore别名
	 * @param data  待签名数据
	 * @return 签名结果
	 */
	@Deprecated

	public static byte[] sign(String alias, byte[] data) {
		return sign(alias, data, false);
	}

	/**
	 * 验签，2048 bit 密钥，已不推荐使用，标记为Deprecated
	 *
	 * @param alias     keystore别名
	 * @param data      待验签数据
	 * @param signValue 签名值
	 * @return 验签结果
	 */
	@Deprecated

	public static boolean verifySign(String alias, byte[] data, byte[] signValue) {
		return verifySign(alias, data, signValue, false);
	}

	/**
	 * 签名byte[] 数据
	 *
	 * @param alias keystore别名
	 * @param data  待签名数据
	 * @return 签名结果
	 */

	public static byte[] signNew(String alias, byte[] data) {
		return sign(alias, data, true);
	}

	/**
	 * 验签
	 *
	 * @param alias     keystore别名
	 * @param data      待验签数据
	 * @param signValue 签名值
	 * @return 验签结果
	 */

	public static boolean verifySignNew(String alias, byte[] data, byte[] signValue) {
		return verifySign(alias, data, signValue, true);
	}


	private static byte[] sign(String alias, byte[] data, boolean is3072) {
		byte[] result = new byte[0];
		if (TextUtils.isEmpty(alias) || data == null) {
			LogUtil.e(TAG, "alias or content is null");
			return result;
		}
		if (!isBuildVersionHigherThan22()) {
			LogUtil.e(TAG, "sdk version is too low");
			return result;
		}

		KeyStore.Entry entry = loadEntry(alias, is3072);
		if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
			LogUtil.e(TAG, "Not an instance of a PrivateKeyEntry");
			return result;
		}
		Signature signature = null;
		try {
			signature = Signature.getInstance(RSA_MODE_SIGN);
			signature.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
			signature.update(data);
			return signature.sign();
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (SignatureException e) {
			LogUtil.e(TAG, "SignatureException: " + e.getMessage());
		} catch (InvalidKeyException e) {
			LogUtil.e(TAG, "InvalidKeyException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception: " + e.getMessage());
		}
		return result;
	}


	private static boolean verifySign(String alias, byte[] data, byte[] signValue, boolean is3072) {
		if (TextUtils.isEmpty(alias) || data == null || signValue == null) {
			LogUtil.e(TAG, "alias or content or sign value is null");
			return false;
		}
		boolean result = false;
		if (!isBuildVersionHigherThan22()) {
			LogUtil.e(TAG, "sdk version is too low");
			return false;
		}

		KeyStore.Entry entry = loadEntry(alias, is3072);
		if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
			LogUtil.e(TAG, "Not an instance of a PrivateKeyEntry");
			return false;
		}
		Signature signature = null;
		try {
			signature = Signature.getInstance(RSA_MODE_SIGN);
			signature.initVerify(((KeyStore.PrivateKeyEntry) entry).getCertificate());
			signature.update(data);
			result = signature.verify(signValue);
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (SignatureException e) {
			LogUtil.e(TAG, "SignatureException: " + e.getMessage());
		} catch (InvalidKeyException e) {
			LogUtil.e(TAG, "InvalidKeyException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception: " + e.getMessage());
		}
		return result;
	}

	/**
	 * 生成公私钥对
	 *
	 * @param alias keystore别名
	 * @return 公私钥对 key pair
	 */

	@RequiresApi(api = Build.VERSION_CODES.M)
	private synchronized static KeyPair generateKeyPair(String alias, boolean is3072) {
		if (keyPairExists(alias)) {
			LogUtil.e(TAG, "Key pair exits");
			return null;
		}
		KeyPairGenerator keyPairGenerator;
		KeyPair keyPair = null;

		try {
			keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
			if (!is3072) {
				keyPairGenerator.initialize(
						new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
								.setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
								.setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
								.setKeySize(KEY_LENGTH)
								.build());
			} else {
				keyPairGenerator.initialize(
						new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
								.setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
								.setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
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
		}
		return keyPair;
	}

	/**
	 * 根据keyStore获取公私钥需要的entry
	 *
	 * @param alias keyStore别名
	 * @return Entry
	 */

	@RequiresApi(api = Build.VERSION_CODES.M)
	private static KeyStore.Entry loadEntry(String alias, boolean is30721) {
		if (!keyPairExists(alias)) {
			generateKeyPair(alias, is30721);
		}
		KeyStore.Entry entry = null;
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
			keyStore.load(null);
			/*
			 * getEntry在P版本上会抛出KeyStore的异常信息，但是不影响签名，系统暂时未解决：参考：https://github.com/adorsys/secure-storage-android/issues
			 * /30
			 */
			entry = keyStore.getEntry(alias, null);
		} catch (KeyStoreException e) {
			LogUtil.e(TAG, "KeyStoreException: " + e.getMessage());
		} catch (CertificateException e) {
			LogUtil.e(TAG, "CertificateException: " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (IOException e) {
			LogUtil.e(TAG, "IOException: " + e.getMessage());
		} catch (UnrecoverableEntryException e) {
			LogUtil.e(TAG, "UnrecoverableEntryException: " + e.getMessage());
		}
		return entry;
	}

	/**
	 * 判断当前系统版本是否大于等于M
	 *
	 * @return
	 */
	public static boolean isBuildVersionHigherThan22() {
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
		}
		return false;
	}
}
