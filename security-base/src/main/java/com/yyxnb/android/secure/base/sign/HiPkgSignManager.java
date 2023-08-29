package com.yyxnb.android.secure.base.sign;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.text.TextUtils;

import com.yyxnb.android.secure.utils.LogUtil;


/**
 * 证书指纹获取类
 * keytool -printcert -file CERT.RSA ，查看证书SHA256值
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/8/29
 */
public class HiPkgSignManager {

    private static final String TAG = "HiPkgSignManager";

    /**
     * 返回证书签名
     *
     * @param packageName Apk 包名
     * @return
     */
    public static byte[] getInstalledAPPSignature(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            LogUtil.e(TAG, "packageName is null or context is null");
            return new byte[0];
        }
        PackageInfo foundPkgInfo;
        try {
            PackageManager mPackageManager = context.getPackageManager();
            if (mPackageManager != null) {
                foundPkgInfo = mPackageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                if (foundPkgInfo != null) {
                    Signature signature = foundPkgInfo.signatures[0];
                    return signature.toByteArray();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, "PackageManager.NameNotFoundException : " + e.getMessage(), true);
        } catch (Exception e) {
            LogUtil.e(TAG, "Exception : " + e.getMessage(), true);
        }

        return new byte[0];
    }


    /**
     * 返回某未安装APK的证书签名信息
     *
     * @param context         上下文
     * @param archiveFilePath APK完整路径
     * @return 证书签名信息
     */
    public static byte[] getUnInstalledAPPSignature(Context context, String archiveFilePath) {
        if (context == null || TextUtils.isEmpty(archiveFilePath)) {
            LogUtil.e(TAG, "archiveFilePath is null or context is null");
            return new byte[0];
        }

        PackageInfo foundPkgInfo = getUnInstalledAPPPackageInfo(context, archiveFilePath);
        if (foundPkgInfo != null) {
            Signature signature = foundPkgInfo.signatures[0];
            if (signature != null) {
                return signature.toByteArray();
            }
        } else {
            LogUtil.e(TAG, "PackageInfo is null ");
        }
        return new byte[0];
    }

    /**
     * 返回某未安装APP的包名
     *
     * @param context         上下文
     * @param archiveFilePath APK完整路径
     * @return 包名
     */
    public static String getUnInstalledAPPPackageName(Context context, String archiveFilePath) {

        if (context == null || TextUtils.isEmpty(archiveFilePath)) {
            LogUtil.e(TAG, "archiveFilePath is null or context is null");
            return "";
        }

        PackageInfo foundPkgInfo = getUnInstalledAPPPackageInfo(context, archiveFilePath);
        if (foundPkgInfo != null) {
            return foundPkgInfo.packageName;
        }
        return "";
    }

    private static PackageInfo getUnInstalledAPPPackageInfo(Context context, String archiveFilePath) {
        try {
            PackageManager mPackageManager = context.getPackageManager();
            if (mPackageManager != null) {
                return mPackageManager.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_SIGNATURES);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Exception : " + e.getMessage(), true);
        }
        return null;
    }

    /**
     * 返回某APP证书指纹信息
     *
     * @param context           上下文
     * @param targetPackageName 指定的 Apk 包名
     * @return
     */
    public static String getInstalledAppHash(Context context, String targetPackageName) {
        String byteArrayToHex = "";
        byte[] signatures = getInstalledAPPSignature(context, targetPackageName);
        if (signatures != null && signatures.length > 0) {
            byteArrayToHex = encryByteBySHA256(signatures);
        }
        return byteArrayToHex;
    }

    /**
     * 获取某未安装APP的证书指纹信息
     *
     * @param context
     * @param targetArchivePath Apk完整路径
     * @return 证书指纹信息
     */
    public static String getUnInstalledAppHash(Context context, String targetArchivePath) {
        String hash = "";
        byte[] signatures = getUnInstalledAPPSignature(context, targetArchivePath);
        if (signatures != null && signatures.length > 0) {
            hash = encryByteBySHA256(signatures);
        }
        return hash;
    }

    /**
     * <sha256>
     *
     * @param data 原始加密字节数组
     * @return 经 SHA-256 后的字符串
     */
    private static String encryByteBySHA256(byte[] data) {
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] array = md.digest(data);
            hash = byteArrayToHexString(array);
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(TAG, "NoSuchAlgorithmException" + e.getMessage());
        }
        return hash;
    }

    /**
     * byte数组转成16进制 方法表述
     *
     * @param data 原始 byte 数组
     * @return String
     */
    private static String byteArrayToHexString(byte[] data) {
        if (data == null) {
            return null;
        } else {
            int len = data.length;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                if ((data[i] & 0xFF) < 16) {
                    sb.append("0" + Integer.toHexString(data[i] & 0xFF));
                } else {
                    sb.append(Integer.toHexString(data[i] & 0xFF));
                }
            }
            return sb.toString().toUpperCase(Locale.ENGLISH);
        }
    }

    /**
     * 校验某已安装的APK的签名值是否和预期的一样用来判断此APK是否被篡改，可校验本APK，也可以校验其它APK
     *
     * @param preSign 要校验的APK的证书的指纹信息（SHA256）的真实值，通过keytool -printcert -file CERT.RSA查看证书的SHA256信息
     * @param pkgName 待校验APK的包名
     * @param context 校验处的上下文
     * @return Apk 是否篡改
     */
    public static boolean doCheckInstalled(Context context, String preSign, String pkgName) {
        if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(preSign) || context == null) {
            return false;
        }
        String installedAppHash = getInstalledAppHash(context, pkgName);
        return preSign.equalsIgnoreCase(installedAppHash);
    }

    /**
     * 校验某未安装的APK的签名值是否和预期的一样用来判断此APK是否被篡改。同时校验包名和证书指纹信息
     *
     * @param context        上下文
     * @param preSign        要校验的APK的证书的指纹信息（SHA256）的真实值
     * @param apkArchivePath 要校验的APK的完整路径
     * @param pkgName        要校验的APK包名
     * @return 校验结果
     */
    public static boolean doCheckArchiveApk(Context context, String preSign, String apkArchivePath, String pkgName) {
        if (TextUtils.isEmpty(apkArchivePath) || TextUtils.isEmpty(preSign) || context == null || TextUtils.isEmpty(pkgName)) {
            return false;
        }

        PackageInfo packageInfo = getUnInstalledAPPPackageInfo(context, apkArchivePath);
        if (packageInfo != null) {
            Signature signature = packageInfo.signatures[0];
            byte[] signe = signature.toByteArray();
            String hash = encryByteBySHA256(signe);
            String unInstallAPPPackageName = packageInfo.packageName;
            return preSign.equalsIgnoreCase(hash) && pkgName.equals(unInstallAPPPackageName);
        }
        return false;
    }

    /**
     * 校验某已安装的APK的V3签名值是否和预期的一样用来判断此APK是否被篡改，可校验本APK，也可以校验其它APK
     *
     * @param preSign 要校验的APK的证书的V2,V3指纹信息（SHA256）的真实值，可使用jadx查看证书的SHA256信息
     * @param pkgName 待校验APK的包名
     * @param context 校验处的上下文
     * @return Apk 是否篡改
     */
    public static boolean doCheckInstalledV2V3(Context context, List<String> preSign, String pkgName) {
        if (TextUtils.isEmpty(pkgName) || preSign == null || context == null) {
            return false;
        }
        List<String> signatures = getInstalledAppHashV2V3(context, pkgName);
        if (signatures == null) {
            return false;
        }
        List<String> temp = new ArrayList<>();
        for (String sign : preSign) {
            temp.add(sign.toUpperCase(Locale.ENGLISH));
        }
        for (String sign : signatures) {
            if (!temp.contains(sign)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取已安装apk的V2,V3签名的证书信息
     *
     * @param context     context
     * @param packageName packageName
     * @return 证书信息
     */
    public static List<String> getInstalledAppHashV2V3(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                if (packageInfo == null || packageInfo.signingInfo == null) {
                    return null;
                }
                if (packageInfo.signingInfo.hasMultipleSigners()) {
                    return signatureDigest(packageInfo.signingInfo.getApkContentsSigners());
                } else {
                    return signatureDigest(packageInfo.signingInfo.getSigningCertificateHistory());
                }
            } else {
                @SuppressLint("PackageManagerGetSignatures")
                PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                if (packageInfo == null
                        || packageInfo.signatures == null
                        || packageInfo.signatures.length == 0
                        || packageInfo.signatures[0] == null) {
                    return null;
                }
                return signatureDigest(packageInfo.signatures);
            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        } catch (Exception e) {
            return null;
        } catch (Throwable throwable) {
            return null;
        }
    }

    private static List<String> signatureDigest(Signature[] apkContentsSigners) {
        List<String> result = new ArrayList<>();
        if (apkContentsSigners == null || apkContentsSigners.length == 0) {
            return result;
        }

        for (Signature signature : apkContentsSigners) {
            result.add(encryByteBySHA256(signature.toByteArray()));
        }
        return result;
    }

}
