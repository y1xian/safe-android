package com.yyxnb.android.secure.utils;

import android.annotation.SuppressLint;
import android.os.Build;

/**
 * 根密钥导出工具
 * 根据三段根密钥组件和一段盐值，使用PBKDF算法导出根密钥对象，用于对工作密钥进行加解密
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/3/24
 */
public class RootKeyUtil {
	private static final String TAG = RootKeyUtil.class.getSimpleName();
	private byte[] rootKey = null;

	private RootKeyUtil() {
	}

	/**
	 * 根据根密钥组件导出根密钥，要求每段都是hex形式，长度不低于32byte
	 *
	 * @param firstRootKeyComp  第一段根密钥
	 * @param secondRootKeyComp 第二段根密钥
	 * @param thirdRootKeyComp  第三代根密钥
	 * @param salt              盐值，hex形式，不低于32字节
	 * @return 导出的根密钥
	 */
	public static RootKeyUtil newInstance(String firstRootKeyComp, String secondRootKeyComp, String thirdRootKeyComp, String salt) {
		RootKeyUtil keyObj = new RootKeyUtil();
		keyObj.initRootKey(firstRootKeyComp, secondRootKeyComp, thirdRootKeyComp, salt);
		return keyObj;
	}

	/**
	 * 根据根密钥组件导出根密钥，要求每段都是hex形式，长度不低于32byte
	 *
	 * @param firstRootKeyComp  第一段根密钥
	 * @param secondRootKeyComp 第二段根密钥
	 * @param thirdRootKeyComp  第三代根密钥
	 * @param salt              盐值，不低于16字节
	 * @return 导出的根密钥
	 */
	public static RootKeyUtil newInstance(String firstRootKeyComp, String secondRootKeyComp, String thirdRootKeyComp, byte[] salt) {
		RootKeyUtil keyObj = new RootKeyUtil();
		keyObj.initRootKey(firstRootKeyComp, secondRootKeyComp, thirdRootKeyComp, salt);
		return keyObj;
	}

	private void initRootKey(String first, String second, String third, String salt) {
		initRootKey(first, second, third, HexUtil.hexStr2ByteArray(salt));
	}


	/**
	 * 获取根密钥
	 *
	 * @return 返回根密钥
	 */
	public byte[] getRootKey() {
		return rootKey.clone();
	}

	/**
	 * 返回hex形式根密钥
	 *
	 * @return hex形式根密钥
	 */
	public String getRootKeyHex() {
		return HexUtil.byteArray2HexStr(rootKey);
	}

	@SuppressLint("NewApi")
	private void initRootKey(String first, String second, String third, byte[] salt) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			LogUtil.i(TAG, "initRootKey: sha1");
			rootKey = BaseKeyUtil.exportRootKey(first, second, third, salt, false);
		} else {
			LogUtil.i(TAG, "initRootKey: sha256");
			rootKey = BaseKeyUtil.exportRootKey(first, second, third, salt, true);
		}
	}
}
