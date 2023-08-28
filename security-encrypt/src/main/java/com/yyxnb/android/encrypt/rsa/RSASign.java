package com.yyxnb.android.encrypt.rsa;

import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import com.yyxnb.android.encrypt.utils.EncryptUtil;
import com.yyxnb.android.encrypt.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Arrays;

/**
 * RSA签名和验签。高于 23 的版本应使用安全的签名和验签算法（SHA256WithRSA/PSS）
 * 名称带new 的接口 @RequiresApi(api = Build.VERSION_CODES.M)
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/26
 */
public abstract class RSASign {

	// 无填充方式，相同私钥和明文，签名结果相同，不推荐使用
	private static final String ALGORITHM = "SHA256WithRSA";

	// https://developer.android.com/reference/java/security/Signature
	// 24才开始支持PSS（链接中23+—指的是24才开始，不包含23）
	private static final String SHA256WithRSA_PSS_ALGORITHM = "SHA256WithRSA/PSS";

	private static final String TAG = RSASign.class.getSimpleName();

	private static final String CHARSET = "UTF-8";

	private static final String EMPTY = "";

	/**
	 * RSA签名，私钥签名
	 *
	 * @param content    待签名数据
	 * @param privateKey 字符串形式的私钥
	 * @return 签名值 ，只要私钥相同，待签名内容相同，则签名结果相同，无填充方式，不推荐使用
	 */
	@Deprecated
	public static String sign(String content, String privateKey) {
		return sign(content, privateKey, false);
	}

	/**
	 * SHA256WithRSA/PSS 签名
	 *
	 * @param content    待签名数据
	 * @param privateKey 字符串形式的私钥
	 * @return 签名值 ，私钥相同，待签名内容相同，签名结果也不通
	 */

	public static String newSign(String content, String privateKey) {
		if (!isBuildVersionHigherThan23()) {
			LogUtil.e(TAG, "sdk version is too low");
			return EMPTY;
		}
		return sign(content, privateKey, true);
	}

	private static String sign(String content, String privateKey, boolean isNewSign) {
		if (TextUtils.isEmpty(content) || TextUtils.isEmpty(privateKey)) {
			LogUtil.e(TAG, "sign content or key is null");
			return EMPTY;
		}
		PrivateKey priKey = EncryptUtil.getPrivateKey(privateKey);
		if (isNewSign) {
			return newSign(content, priKey);
		} else {
			return sign(content, priKey);
		}
	}

	/**
	 * RSA签名，私钥签名
	 *
	 * @param content    待签名数据
	 * @param privateKey PrivateKey形式的私钥
	 * @return 签名值
	 */
	@Deprecated
	public static String sign(String content, PrivateKey privateKey) {
		return sign(content, privateKey, false);
	}

	/**
	 * SHA256WithRSA/PSS 签名
	 *
	 * @param content    待签名数据
	 * @param privateKey PrivateKey形式的私钥
	 * @return 签名值 ，私钥相同，待签名内容相同，签名结果也不通
	 */

	public static String newSign(String content, PrivateKey privateKey) {
		if (!isBuildVersionHigherThan23()) {
			LogUtil.e(TAG, "sdk version is too low");
			return EMPTY;
		}
		return sign(content, privateKey, true);
	}

	private static String sign(String content, PrivateKey privateKey, boolean isNewSign) {
		try {
			return Base64.encodeToString(sign(content.getBytes(CHARSET), privateKey, isNewSign), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "sign UnsupportedEncodingException: " + e.getMessage());
		}
		return EMPTY;
	}

	public static byte[] sign(byte[] content, PrivateKey privateKey, boolean isNewSign) {
		byte[] result = new byte[0];
		if (content == null || privateKey == null || !RSAEncrypt.isPrivateKeyLengthRight((RSAPrivateKey) privateKey)) {
			LogUtil.e(TAG, "content or privateKey is null , or length is too short");
			return result;
		}
		Signature signature;
		try {
			if (isNewSign) {
				signature = Signature.getInstance(SHA256WithRSA_PSS_ALGORITHM);
				signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
			} else {
				signature = Signature.getInstance(ALGORITHM);
			}
			signature.initSign(privateKey);
			signature.update(content);
			result = signature.sign();
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "sign NoSuchAlgorithmException: " + e.getMessage());
		} catch (InvalidKeyException e) {
			LogUtil.e(TAG, "sign InvalidKeyException: " + e.getMessage());
		} catch (SignatureException e) {
			LogUtil.e(TAG, "sign SignatureException: " + e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			LogUtil.e(TAG, "sign InvalidAlgorithmParameterException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "sign Exception: " + e.getMessage());
		}
		return result;
	}

	public static byte[] sign(ByteBuffer content, PrivateKey privateKey, boolean isNewSign) {
		byte[] result = new byte[0];
		if (content == null || privateKey == null || !RSAEncrypt.isPrivateKeyLengthRight((RSAPrivateKey) privateKey)) {
			LogUtil.e(TAG, "content or privateKey is null , or length is too short");
			return result;
		}
		Signature signature;
		try {
			if (isNewSign) {
				signature = Signature.getInstance(SHA256WithRSA_PSS_ALGORITHM);
				signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
			} else {
				signature = Signature.getInstance(ALGORITHM);
			}
			signature.initSign(privateKey);
			signature.update(content);
			result = signature.sign();
			LogUtil.i(TAG, "result is : " + Arrays.toString(result));
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "sign NoSuchAlgorithmException: " + e.getMessage());
		} catch (InvalidKeyException e) {
			LogUtil.e(TAG, "sign InvalidKeyException: " + e.getMessage());
		} catch (SignatureException e) {
			LogUtil.e(TAG, "sign SignatureException: " + e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			LogUtil.e(TAG, "sign InvalidAlgorithmParameterException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "sign Exception: " + e.getMessage());
		}
		return result;
	}

	/**
	 * RSA验签，公钥验签
	 *
	 * @param content   待验签数据
	 * @param signVal   签名值
	 * @param publicKey PublicKey形式的公钥
	 * @return the boolean
	 */
	@Deprecated
	public static boolean verifySign(String content, String signVal, String publicKey) {
		return verifySign(content, signVal, publicKey, false);
	}

	/**
	 * SHA256WithRSA/PSS 验签，公钥验签
	 *
	 * @param content   待验签数据
	 * @param signVal   签名值
	 * @param publicKey PublicKey形式的公钥
	 * @return the boolean
	 */

	public static boolean newVerifySign(String content, String signVal, String publicKey) {
		if (!isBuildVersionHigherThan23()) {
			LogUtil.e(TAG, "sdk version is too low");
			return false;
		}
		return verifySign(content, signVal, publicKey, true);
	}

	private static boolean verifySign(String content, String signVal, String publicKey, boolean isNewVerify) {

		if (TextUtils.isEmpty(content) || TextUtils.isEmpty(publicKey) || TextUtils.isEmpty(signVal)) {
			LogUtil.e(TAG, "content or public key or sign value is null");
			return false;
		}
		PublicKey pubKey = EncryptUtil.getPublicKey(publicKey);
		if (isNewVerify) {
			return newVerifySign(content, signVal, pubKey);
		} else {
			return verifySign(content, signVal, pubKey);
		}
	}

	/**
	 * RSA验签，公钥验签
	 *
	 * @param content   待验签数据
	 * @param signVal   签名值
	 * @param publicKey 公钥
	 * @return the boolean
	 */
	@Deprecated
	public static boolean verifySign(String content, String signVal, PublicKey publicKey) {
		return verifySign(content, signVal, publicKey, false);
	}

	/**
	 * SHA256WithRSA/PSS 验签，公钥验签
	 *
	 * @param content   待验签数据
	 * @param signVal   签名值
	 * @param publicKey 公钥
	 * @return the boolean
	 */

	public static boolean newVerifySign(String content, String signVal, PublicKey publicKey) {
		if (!isBuildVersionHigherThan23()) {
			LogUtil.e(TAG, "sdk version is too low");
			return false;
		}
		return verifySign(content, signVal, publicKey, true);
	}

	private static boolean verifySign(String content, String signVal, PublicKey publicKey, boolean isNewVerifySign) {
		try {
			return verifySign(content.getBytes(CHARSET), Base64.decode(signVal, Base64.DEFAULT), publicKey,
					isNewVerifySign);
		} catch (UnsupportedEncodingException e) {
			LogUtil.e(TAG, "verifySign UnsupportedEncodingException: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "base64 decode Exception : " + e.getMessage());
		}
		return false;
	}

	public static boolean verifySign(byte[] content, byte[] signVal, PublicKey publicKey, boolean isNewVerifySign) {
		if (content == null || publicKey == null || signVal == null
				|| !RSAEncrypt.isPublicKeyLengthRight((RSAPublicKey) publicKey)) {
			LogUtil.e(TAG, "content or publicKey is null , or length is too short");
			return false;
		}
		Signature signature;
		try {
			if (isNewVerifySign) {
				signature = Signature.getInstance(SHA256WithRSA_PSS_ALGORITHM);
				signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
			} else {
				signature = Signature.getInstance(ALGORITHM);
			}
			signature.initVerify(publicKey);
			signature.update(content);
			return signature.verify(signVal);
		} catch (GeneralSecurityException e) {
			LogUtil.e(TAG, "check sign exception: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "exception : " + e.getMessage());
		}
		return false;
	}

	public static boolean verifySign(ByteBuffer content, byte[] signVal, PublicKey publicKey, boolean isNewVerifySign) {
		if (content == null || publicKey == null || signVal == null
				|| !RSAEncrypt.isPublicKeyLengthRight((RSAPublicKey) publicKey)) {
			LogUtil.e(TAG, "content or publicKey is null , or length is too short");
			return false;
		}
		Signature signature;
		try {
			if (isNewVerifySign) {
				signature = Signature.getInstance(SHA256WithRSA_PSS_ALGORITHM);
				signature.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
			} else {
				signature = Signature.getInstance(ALGORITHM);
			}
			signature.initVerify(publicKey);
			signature.update(content);
			return signature.verify(signVal);
		} catch (GeneralSecurityException e) {
			LogUtil.e(TAG, "check sign exception: " + e.getMessage());
		} catch (Exception e) {
			LogUtil.e(TAG, "exception : " + e.getMessage());
		}
		return false;
	}

	/**
	 * 判断当前系统版本是否大于M
	 *
	 * @return
	 */
	public static boolean isBuildVersionHigherThan23() {
		return Build.VERSION.SDK_INT > Build.VERSION_CODES.M;
	}

}
