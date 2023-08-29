package com.yyxnb.android.secure.utils;

import android.os.Build;
import android.util.Base64;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * EncryptUtil
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/26
 */
public class EncryptUtil {
	private static final String TAG = "EncryptUtil";
	private static final String RSA_ALGORITHM = "RSA";
	private static boolean bouncycastleFlag = false;
	private static boolean isLogError = true;

	/**
	 * 生成len长度的安全随机数。如果依赖了BC库，且设置flag为true，则调用BC库生成安全随机数
	 *
	 * @return
	 */
	public static byte[] generateSecureRandom(int len) {
		if (!bouncycastleFlag) {
			byte[] randomBytes = new byte[len];
			SecureRandom random = null;
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					// 鸿蒙系统调用失败
					random = SecureRandom.getInstanceStrong();
				}
			} catch (NoSuchAlgorithmException e) {
				LogUtil.e(TAG, "getSecureRandomBytes: NoSuchAlgorithmException");
			}

			try {
				if (random == null) {
					random = SecureRandom.getInstance("SHA1PRNG");
				}
				random.nextBytes(randomBytes);
				return randomBytes;
			} catch (NoSuchAlgorithmException e) {
				LogUtil.e(TAG, "getSecureRandomBytes getInstance: NoSuchAlgorithmException");
			} catch (Exception e) {
				LogUtil.e(TAG, "getSecureRandomBytes getInstance: exception : " + e.getMessage());
			}
		} else {
			return generateSecureRandomNew(len);
		}
		return new byte[0];
	}

	/**
	 * 生成len长度的安全随机数。如果依赖了BC库，且设置flag为true，且系统高于25，则调用BC库生成安全随机数
	 *
	 * @return
	 */
	public static String generateSecureRandomStr(int len) {
		return HexUtil.byteArray2HexStr(generateSecureRandom(len));
	}

	/**
	 * 生成SecureRandom
	 *
	 * @return SecureRandom
	 */
	public static SecureRandom genSecureRandom() {
		if (!bouncycastleFlag) {
			SecureRandom source = null;
			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					source = SecureRandom.getInstanceStrong();
				} else {
					source = SecureRandom.getInstance("SHA1PRNG");
				}
			} catch (NoSuchAlgorithmException e) {
				LogUtil.e(TAG, "genSecureRandom: NoSuchAlgorithmException");
			}
			return source;
		} else {
			return genSecureRandomNew();
		}
	}

	/**
	 * 生成secureRandom对象
	 *
	 * @throws GeneralSecurityException
	 */

	private static SecureRandom genSecureRandomNew() {
		LogUtil.i(TAG, "generateSecureRandomNew ");
		SecureRandom source = null;
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				// 鸿蒙系统调用失败
				source = SecureRandom.getInstanceStrong();
			}
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "getSecureRandomBytes: NoSuchAlgorithmException");
		}

		try {
			if (source == null) {
				source = SecureRandom.getInstance("SHA1PRNG");
			}
			boolean predictionResistant = true; // /dev/random可以认为是活熵源

			// NID_aes_256_ctr
			BlockCipher cipher = new AESEngine();
			int cipherLen = 256;
			int entropyBitesRequired = 384; // 种子长度默认256bit
			// 生成nonce
			byte[] nonce = new byte[cipherLen / 8];
			source.nextBytes(nonce);
			boolean reSeed = false; // 是否每次取完随机数都重新刷新熵源
			return new SP800SecureRandomBuilder(source, predictionResistant)
					.setEntropyBitsRequired(entropyBitesRequired).buildCTR(cipher, cipherLen, nonce, reSeed);
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException");
		} catch (Throwable e) {
			// 如果没有引入bc库，会报类找不到的错误，需要捕获异常，否则会导致应用crash
			if (isLogError) {
				LogUtil.e(TAG, "exception : " + e.getMessage() + " , you should implementation bcprov-jdk15on library");
				isLogError = false;
			}
		}
		return source;
	}

	private static byte[] generateSecureRandomNew(int byteSize) {
		SecureRandom secureRandom = genSecureRandomNew();
		if (secureRandom == null) {
			return new byte[0];
		}
		byte[] bytes = new byte[byteSize];
		secureRandom.nextBytes(bytes);
		return bytes;
	}

	public static boolean isBouncycastleFlag() {
		return bouncycastleFlag;
	}

	public static void setBouncycastleFlag(boolean bouncycastleFlag) {
		LogUtil.i(TAG, "setBouncycastleFlag: " + bouncycastleFlag);
		EncryptUtil.bouncycastleFlag = bouncycastleFlag;
	}

	/**
	 * 从字符串中加载公钥
	 *
	 * @param publicKeyStr 公钥数据字符串
	 * @return RSAPublicKey 公钥
	 * @throws Exception 加载公钥时产生的异常
	 */
	public static RSAPublicKey getPublicKey(String publicKeyStr) {

		byte[] publicKey;
		try {
			publicKey = Base64.decode(publicKeyStr, Base64.DEFAULT);
		} catch (IllegalArgumentException e) {
			LogUtil.e(TAG, "base64 decode IllegalArgumentException");
			return null;
		} catch (Exception e) {
			LogUtil.e(TAG, "base64 decode Exception" + e.getMessage());
			return null;
		}
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (GeneralSecurityException e) {
			LogUtil.e(TAG, "load Key Exception:" + e.getMessage());
		}
		return null;
	}

	/**
	 * 得到私钥
	 *
	 * @param privateKeyStr 密钥字符串（经过base64编码）
	 */
	public static PrivateKey getPrivateKey(String privateKeyStr) {
		byte[] privatekey;
		try {
			privatekey = Base64.decode(privateKeyStr, Base64.DEFAULT);
		} catch (IllegalArgumentException e) {
			LogUtil.e(TAG, "base64 decode IllegalArgumentException");
			return null;
		} catch (Exception e) {
			LogUtil.e(TAG, "base64 decode Exception" + e.getMessage());
			return null;
		}
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privatekey);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			return keyFactory.generatePrivate(keySpec);
		} catch (GeneralSecurityException e) {
			LogUtil.e(TAG, "load Key Exception:" + e.getMessage());
		}
		return null;
	}
}
