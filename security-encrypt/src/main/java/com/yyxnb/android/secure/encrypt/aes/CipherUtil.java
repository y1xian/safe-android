package com.yyxnb.android.secure.encrypt.aes;

import com.yyxnb.android.secure.utils.EncryptUtil;
import com.yyxnb.android.secure.utils.HexUtil;
import com.yyxnb.android.secure.utils.LogUtil;

import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * CipherUtil
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/26
 */
public class CipherUtil {
	private static final String TAG = "CipherUtil";
	private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";
	private static final String AES_CBC_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final String AES_ALGORITHM = "AES";
	private static final String EMPTY = "";
	private static final int AES_GCM_KEY_LEN = 16;
	private static final int AES_GCM_IV_LEN = 12;
	private static final int AES_128_CBC_IV_LEN = 16;

	public static Cipher getAesGcmEncryptCipher(byte[] key) {
		byte[] iv = EncryptUtil.generateSecureRandom(AES_GCM_IV_LEN);
		LogUtil.d(TAG, "getEncryptCipher: iv is : " + HexUtil.byteArray2HexStr(iv));
		return getAesGcmEncryptCipher(key, iv);
	}

	public static int getAesGcmEncryptContentLen(byte[] input, byte[] key) {
		byte[] iv = EncryptUtil.generateSecureRandom(AES_GCM_IV_LEN);
		return getAesGcmEncryptContentLen(input, key, iv);
	}

	public static Cipher getAesGcmDecryptCipher(byte[] key, Cipher encryptCipher) {
		byte[] iv = encryptCipher.getIV();
		return getAesGcmDecryptCipher(key, iv);
	}

	public static Cipher getAesCbcEncryptCipher(byte[] key) {
		byte[] iv = EncryptUtil.generateSecureRandom(AES_128_CBC_IV_LEN);
		return getAesCbcEncryptCipher(key, iv);
	}

	public static int getAesCbcEncryptContentLen(byte[] input, byte[] key) {
		byte[] iv = EncryptUtil.generateSecureRandom(AES_128_CBC_IV_LEN);
		return getAesCbcEncryptContentLen(input, key, iv);
	}

	public static Cipher getAesCbcDecryptCipher(byte[] key, Cipher encryptCipher) {
		byte[] iv = encryptCipher.getIV();
		return getAesCbcDecryptCipher(key, iv);
	}

	/**
	 * 根据key ， iv 生成cipher对象，用于加密
	 *
	 * @param key
	 * @param ivParameter
	 * @return
	 */
	public static Cipher getAesGcmEncryptCipher(byte[] key, byte[] ivParameter) {
		return getAesGcmCipher(key, ivParameter, Cipher.ENCRYPT_MODE);
	}

	/**
	 * 根据content ， key ， iv 获取密文长度
	 * 解密cipher无法获取准确的解密长度，参考：https://stackoverflow.com/questions/43062663/why-would-cipher-getoutputsize-return-value-higher-than-required
	 *
	 * @param input
	 * @param key
	 * @param ivParameter
	 * @return
	 */
	public static int getAesGcmEncryptContentLen(byte[] input, byte[] key, byte[] ivParameter) {
		Cipher cipher = getAesGcmEncryptCipher(key, ivParameter);
		return getOutputLen(cipher, input);
	}

	public static Cipher getAesGcmDecryptCipher(byte[] key, byte[] ivParameter) {
		return getAesGcmCipher(key, ivParameter, Cipher.DECRYPT_MODE);
	}

	/**
	 * 根据key ， iv 生成cipher对象，用于加密
	 *
	 * @param key
	 * @param ivParameter
	 * @return
	 */
	public static Cipher getAesCbcEncryptCipher(byte[] key, byte[] ivParameter) {
		return getAesCbcCipher(key, ivParameter, Cipher.ENCRYPT_MODE);
	}

	/**
	 * 根据content ， key ， iv 获取密文长度
	 *
	 * @param input
	 * @param key
	 * @param ivParameter
	 * @return
	 */
	public static int getAesCbcEncryptContentLen(byte[] input, byte[] key, byte[] ivParameter) {
		Cipher cipher = getAesCbcEncryptCipher(key, ivParameter);
		return getOutputLen(cipher, input);
	}

	public static Cipher getAesCbcDecryptCipher(byte[] key, byte[] ivParameter) {
		return getAesCbcCipher(key, ivParameter, Cipher.DECRYPT_MODE);
	}

	private static Cipher getAesGcmCipher(byte[] key, byte[] ivParameter, int mode) {
		return getCipher(key, ivParameter, mode, AES_GCM_ALGORITHM);
	}

	private static Cipher getAesCbcCipher(byte[] key, byte[] ivParameter, int mode) {
		return getCipher(key, ivParameter, mode, AES_CBC_ALGORITHM);
	}

	private static Cipher getCipher(byte[] key, byte[] ivParameter, int mode, String algorithm) {
		if (key == null || key.length < AES_GCM_KEY_LEN || ivParameter == null || ivParameter.length < AES_GCM_IV_LEN
				|| !AesGcm.isBuildVersionHigherThan19()) {
			LogUtil.e(TAG, "gcm encrypt param is not right");
			return null;
		}
		try {
			SecretKeySpec secretkey = new SecretKeySpec(key, AES_ALGORITHM);
			Cipher cipher = Cipher.getInstance(algorithm);
			AlgorithmParameterSpec algorithmParameterSpec = null;
			if (AES_GCM_ALGORITHM.equals(algorithm)) {
				algorithmParameterSpec = AesGcm.getGcmAlgorithmParams(ivParameter);
			} else {
				algorithmParameterSpec = new IvParameterSpec(ivParameter);
			}
			cipher.init(mode, secretkey, algorithmParameterSpec);
			return cipher;
		} catch (GeneralSecurityException e) {
			LogUtil.e(TAG, "GCM encrypt data error" + e.getMessage());
		}
		return null;
	}

	private static int getOutputLen(Cipher cipher, byte[] input) {
		if (cipher != null && input != null) {
			return cipher.getOutputSize(input.length);
		} else {
			return -1;
		}
	}

	/**
	 * @param cipher
	 * @param input  输入（待加密数据）
	 * @param output 输出（待解密数据的存储空间）
	 * @return
	 */
	public static int getContent(Cipher cipher, byte[] input, byte[] output) {
		if (cipher == null || input == null) {
			LogUtil.e(TAG, "getEncryptCOntent: cipher is null or content is null");
			return -1;
		}

		try {
			return cipher.doFinal(input, 0, input.length, output);
		} catch (BadPaddingException e) {
			LogUtil.e(TAG, "getContent: BadPaddingException");
		} catch (IllegalBlockSizeException e) {
			LogUtil.e(TAG, "getContent: IllegalBlockSizeException");
		} catch (ShortBufferException e) {
			LogUtil.e(TAG, "getContent: ShortBufferException");
		}
		return -1;
	}

	/**
	 * Encrypts or decrypts data in a single-part operation, or finishes a
	 * multiple-part operation. The data is encrypted or decrypted,
	 * depending on how this cipher was initialized.
	 * <p>
	 * The first <code>inputLen</code> bytes in the <code>input</code>
	 * buffer, starting at <code>inputOffset</code> inclusive, and any input
	 * bytes that may have been buffered during a previous
	 * <code>update</code> operation, are processed, with padding
	 * (if requested) being applied.
	 * If an AEAD mode such as GCM/CCM is being used, the authentication
	 * tag is appended in the case of encryption, or verified in the
	 * case of decryption.
	 * The result is stored in the <code>output</code> buffer, starting at
	 * <code>outputOffset</code> inclusive.
	 * <p>
	 * If the <code>output</code> buffer is too small to hold the result,
	 * a <code>ShortBufferException</code> is thrown. In this case, repeat this
	 * call with a larger output buffer. Use
	 * {@link #getOutputSize(int) getOutputSize} to determine how big
	 * the output buffer should be.
	 * <p>
	 * Upon finishing, this method resets this cipher object to the state
	 * it was in when previously initialized via a call to <code>init</code>.
	 * That is, the object is reset and available to encrypt or decrypt
	 * (depending on the operation mode that was specified in the call to
	 * <code>init</code>) more data.
	 * <p>
	 * Note: if any exception is thrown, this cipher object may need to
	 * be reset before it can be used again.
	 * <p>
	 * Note: this method should be copy-safe, which means the
	 * <code>input</code> and <code>output</code> buffers can reference
	 * the same byte array and no unprocessed input data is overwritten
	 * when the result is copied into the output buffer.
	 *
	 * @param input        the input buffer
	 * @param inputOffset  the offset in <code>input</code> where the input
	 *                     starts
	 * @param inputLen     the input length
	 * @param output       the buffer for the result
	 * @param outputOffset the offset in <code>output</code> where the result
	 *                     is stored
	 * @return the number of bytes stored in <code>output</code>
	 * @throws IllegalStateException     if this cipher is in a wrong state
	 *                                   (e.g., has not been initialized)
	 * @throws IllegalBlockSizeException if this cipher is a block cipher,
	 *                                   no padding has been requested (only in encryption mode), and the
	 *                                   total
	 *                                   input length of the data processed by this cipher is not a
	 *                                   multiple of
	 *                                   block size; or if this encryption algorithm is unable to
	 *                                   process the input data provided.
	 * @throws ShortBufferException      if the given output buffer is too small
	 *                                   to hold the result
	 * @throws BadPaddingException       if this cipher is in decryption mode,
	 *                                   and (un)padding has been requested, but the decrypted data is not
	 *                                   bounded by the appropriate padding bytes
	 * @throws AEADBadTagException       if this cipher is decrypting in an
	 *                                   AEAD mode (such as GCM/CCM), and the received authentication tag
	 *                                   does not match the calculated value
	 */
	public static int getContent(Cipher cipher, byte[] input, int inputOffset, int inputLen, byte[] output,
								 int outputOffset) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
		if (cipher == null || input == null) {
			LogUtil.e(TAG, "getEncryptCOntent: cipher is null or content is null");
			return -1;
		}
		return cipher.doFinal(input, inputOffset, inputLen, output, outputOffset);
	}

	public static byte[] getContent(Cipher cipher, byte[] input) {
		if (cipher == null || input == null) {
			LogUtil.e(TAG, "getEncryptCOntent: cipher is null or content is null");
			return new byte[0];
		}

		try {
			return cipher.doFinal(input, 0, input.length);
		} catch (BadPaddingException e) {
			LogUtil.e(TAG, "getContent: BadPaddingException");
		} catch (IllegalBlockSizeException e) {
			LogUtil.e(TAG, "getContent: IllegalBlockSizeException");
		}
		return new byte[0];
	}
}
