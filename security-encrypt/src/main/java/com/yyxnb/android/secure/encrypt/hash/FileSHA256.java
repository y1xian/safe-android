package com.yyxnb.android.secure.encrypt.hash;

import android.text.TextUtils;

import com.yyxnb.android.secure.utils.HexUtil;
import com.yyxnb.android.secure.utils.IOUtil;
import com.yyxnb.android.secure.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 对一个文件求SHA256摘要
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/24
 */
public abstract class FileSHA256 {
	private static final int BUFFERSIZE = 1024 * 8;

	private static final String DEFAULTALGORITHIM = "SHA-256";

	private static final String TAG = FileSHA256.class.getSimpleName();

	private static final String EMPTY = "";

	private static final String[] SAFE_ALGORITHM = {"SHA-256", "SHA-384", "SHA-512"};

	/**
	 * 求文件的SHA256值，默认是SHA256算法
	 *
	 * @param file the file
	 * @return string
	 */
	public static String fileSHA256Encrypt(File file) {
		return fileSHAEncrypt(file, DEFAULTALGORITHIM);
	}

	/**
	 * 求一个文件的摘要信息，可以指定HASH方法，如SHA256，SHA384，SHA-512等
	 *
	 * @param file      待哈希的文件
	 * @param algorithm 指定采用什么算法进行哈希
	 * @return 摘要值 （十六进制字符串形式）
	 */
	public static String fileSHAEncrypt(File file, String algorithm) {
		if (TextUtils.isEmpty(algorithm) || !isLegalAlgorithm(algorithm)) {
			LogUtil.e(TAG, "algorithm is empty or not safe");
			return EMPTY;
		}

		if (!isValidFile(file)) {
			LogUtil.e(TAG, "file is not valid");
			return EMPTY;
		}
		String hashValue = null;
		FileInputStream fis = null;
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			fis = new FileInputStream(file);
			byte[] buffer = new byte[BUFFERSIZE];
			int length;
			boolean hasUpdate = false;
			while ((length = fis.read(buffer)) > 0) {
				md.update(buffer, 0, length);
				hasUpdate = true;
			}
			if (hasUpdate) {
				hashValue = HexUtil.byteArray2HexStr(md.digest());
			}
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "NoSuchAlgorithmException" + e.getMessage());
		} catch (IOException e) {
			LogUtil.e(TAG, "IOException" + e.getMessage());
		} finally {
			IOUtil.closeSecure(fis);
		}
		return hashValue;
	}

	/**
	 * 对inputstream求SHA256值
	 *
	 * @param is
	 * @return
	 */
	public static String inputStreamSHA256Encrypt(InputStream is) {
		if (is == null) {
			return EMPTY;
		}
		return inputStreamSHAEncrypt(is, DEFAULTALGORITHIM);
	}

	/**
	 * 对文件流求SHA
	 *
	 * @param is the is
	 * @return string
	 */
	public static String inputStreamSHAEncrypt(InputStream is, String algorithm) {
		if (is == null) {
			return EMPTY;
		}
		byte[] buffer = new byte[BUFFERSIZE];
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			int length;
			while ((length = is.read(buffer)) >= 0) {
				if (length > 0) {
					md.update(buffer, 0, length);
				}
			}
			return HexUtil.byteArray2HexStr(md.digest());
		} catch (IOException | NoSuchAlgorithmException e) {
			LogUtil.e(TAG, "inputstraem exception");
		} finally {
			IOUtil.closeSecure(is);
		}
		return EMPTY;
	}

	/**
	 * file文件完整性校验
	 *
	 * @param file      the file
	 * @param hashValue 哈希之后的值
	 * @return 校验是否通过 boolean
	 */
	public static boolean validateFileSHA256(File file, String hashValue) {
		if (TextUtils.isEmpty(hashValue)) {
			return false;
		}
		return hashValue.equalsIgnoreCase(fileSHA256Encrypt(file));
	}

	/**
	 * @param file
	 * @param hashValue
	 * @return
	 */
	public static boolean validateFileSHA(File file, String hashValue, String algorithm) {
		if (TextUtils.isEmpty(hashValue) || !isLegalAlgorithm(algorithm)) {
			LogUtil.e(TAG, "hash value is null || algorithm is illegal");
			return false;
		}
		return hashValue.equals(fileSHAEncrypt(file, algorithm));
	}

	/**
	 * inputstream完整性校验
	 *
	 * @param is        the is
	 * @param hashValue the hash value
	 * @return boolean
	 */
	public static boolean validateInputStreamSHA256(InputStream is, String hashValue) {
		if (TextUtils.isEmpty(hashValue)) {
			return false;
		}
		return hashValue.equals(inputStreamSHA256Encrypt(is));
	}

	/**
	 * inputstream完整性校验
	 *
	 * @param is        the is
	 * @param hashValue the hash value
	 * @return boolean
	 */
	public static boolean validateInputStreamSHA(InputStream is, String hashValue, String algorithm) {
		if (TextUtils.isEmpty(hashValue) || !isLegalAlgorithm(algorithm)) {
			LogUtil.e(TAG, "hash value is null || algorithm is illegal");
			return false;
		}
		return hashValue.equals(inputStreamSHAEncrypt(is, algorithm));
	}

	/**
	 * 判断文件是否有效
	 *
	 * @param file
	 * @return
	 */
	private static boolean isValidFile(File file) {
		return file != null && file.exists() && file.length() > 0;
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
