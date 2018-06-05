package com.yyx.yyxframe.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA 工具类
 */
public class YRSAUtils {

    /**
     * 随机获取密钥(公钥和私钥), 客户端公钥加密，服务器私钥解密
     *
     * @return 结果密钥对
     * @throws Exception 异常
     */
    public static Map<String, Object> getKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = getKeyPairGenerator();
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<>(2);
        keyMap.put("RSAPublicKey", publicKey);
        keyMap.put("RSAPrivateKey", privateKey);
        return keyMap;
    }

    /**
     * 获取公钥/私钥
     *
     * @param keyMap      密钥对
     * @param isPublicKey true：获取公钥，false：获取私钥
     * @return 获取密钥字符串
     */
    public static String getKey(Map<String, Object> keyMap, boolean isPublicKey) {
        Key key = (Key) keyMap.get(isPublicKey ? "RSAPublicKey" : "RSAPrivateKey");
        return new String(Base64.encode(key.getEncoded(), Base64.DEFAULT));
    }

    /**
     * 获取数字签名
     *
     * @param data       二进制位
     * @param privateKey 私钥(BASE64编码)
     * @return 数字签名结果字符串
     * @throws Exception 异常
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey.getBytes(), Base64.DEFAULT);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = getKeyFactory();
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateK);
        signature.update(data);
        return new String(Base64.encode(signature.sign(), Base64.DEFAULT));
    }

    /**
     * 数字签名校验
     *
     * @param data      二进位组
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名字符串
     * @return true：校验成功，false：校验失败
     * @throws Exception 异常
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey.getBytes(), Base64.DEFAULT);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = getKeyFactory();
        PublicKey publicK = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decode(sign.getBytes(), Base64.DEFAULT));
    }

    /**
     * 获取 KeyFactory
     *
     * @throws NoSuchAlgorithmException 异常
     */
    @SuppressLint("ObsoleteSdkInt")
    private static KeyFactory getKeyFactory() throws NoSuchAlgorithmException,
            NoSuchProviderException {
        KeyFactory keyFactory;
        if (Build.VERSION.SDK_INT >= 16) {
            keyFactory = KeyFactory.getInstance("RSA", "BC");
        } else {
            keyFactory = KeyFactory.getInstance("RSA");
        }
        return keyFactory;
    }

    /**
     * 获取 KeyFactory
     *
     * @throws NoSuchAlgorithmException 异常
     */
    @SuppressLint("ObsoleteSdkInt")
    private static KeyPairGenerator getKeyPairGenerator() throws NoSuchProviderException,
            NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen;
        if (Build.VERSION.SDK_INT >= 16) {
            keyPairGen = KeyPairGenerator.getInstance("RSA", "BC");
        } else {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        }
        return keyPairGen;
    }

    /**
     * 通过私钥byte[]将公钥还原，适用于RSA算法
     *
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException,
            InvalidKeySpecException
    {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 使用N、e值还原公钥
     *
     * @param modulus
     * @param publicExponent
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKey(String modulus, String publicExponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        BigInteger bigIntModulus = new BigInteger(modulus);
        BigInteger bigIntPrivateExponent = new BigInteger(publicExponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 使用N、d值还原私钥
     *
     * @param modulus
     * @param privateExponent
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKey(String modulus, String privateExponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        BigInteger bigIntModulus = new BigInteger(modulus);
        BigInteger bigIntPrivateExponent = new BigInteger(privateExponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     *            公钥数据字符串
     * @throws Exception
     *             加载公钥时产生的异常
     */
    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception
    {
        try
        {
            byte[] buffer = YEncodeUtils.base64Encode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e)
        {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e)
        {
            throw new Exception("公钥非法");
        } catch (NullPointerException e)
        {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从字符串中加载私钥<br>
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
     *
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception
    {
        try
        {
            byte[] buffer = YEncodeUtils.base64Encode(privateKeyStr);
            // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e)
        {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e)
        {
            throw new Exception("私钥非法");
        } catch (NullPointerException e)
        {
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @param in
     *            公钥输入流
     * @throws Exception
     *             加载公钥时产生的异常
     */
    public static PublicKey loadPublicKey(InputStream in) throws Exception
    {
        try
        {
            return loadPublicKey(readKey(in));
        } catch (IOException e)
        {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e)
        {
            throw new Exception("公钥输入流为空");
        }
    }

    /**
     * 从文件中加载私钥
     *
     * @param in
     *            私钥文件名
     * @return 是否成功
     * @throws Exception
     */
    public static PrivateKey loadPrivateKey(InputStream in) throws Exception
    {
        try
        {
            return loadPrivateKey(readKey(in));
        } catch (IOException e)
        {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e)
        {
            throw new Exception("私钥输入流为空");
        }
    }

    /**
     * 读取密钥信息
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static String readKey(InputStream in) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String readLine = null;
        StringBuilder sb = new StringBuilder();
        while ((readLine = br.readLine()) != null)
        {
            if (readLine.charAt(0) == '-')
            {
                continue;
            } else
            {
                sb.append(readLine);
                sb.append('\r');
            }
        }

        return sb.toString();
    }


    /**
     * Rsa公钥加密类型
     */
    public static final int RSA_PUBLIC_ENCRYPT = 0;

    /**
     * Rsa公钥解密类型
     */
    public static final int RSA_PUBLIC_DECRYPT = 1;

    /**
     * Rsa私钥加密类型
     */
    public static final int RSA_PRIVATE_ENCRYPT = 2;

    /**
     * Rsa私钥解密类型
     */
    public static final int RSA_PRIVATE_DECRYPT = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RSA_PUBLIC_ENCRYPT, RSA_PUBLIC_DECRYPT, RSA_PRIVATE_ENCRYPT, RSA_PRIVATE_DECRYPT})
    @interface RSAType {}

    /**
     * Rsa加密/解密（一般情况下，公钥加密私钥解密）
     *
     * @param data   源数据
     * @param string 密钥(BASE64编码)
     * @param type   操作类型：{@link #RSA_PUBLIC_ENCRYPT}，{@link #RSA_PUBLIC_DECRYPT
     *               }，{@link #RSA_PRIVATE_ENCRYPT}，{@link #RSA_PRIVATE_DECRYPT}
     * @return 加密/解密结果字符串
     * @throws Exception 异常
     */
    public static byte[] rsa(byte[] data, String string, @RSAType int type) throws Exception {
        byte[] keyBytes = Base64.decode(string, Base64.DEFAULT);

        Key key;
        KeyFactory keyFactory = getKeyFactory();
        if (type == RSA_PUBLIC_ENCRYPT || type == RSA_PUBLIC_DECRYPT) {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            key = keyFactory.generatePublic(x509KeySpec);
        } else {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            key = keyFactory.generatePrivate(pkcs8KeySpec);
        }

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;

        // 对数据分段加密
        if (type == RSA_PUBLIC_ENCRYPT || type == RSA_PRIVATE_ENCRYPT) {
            cipher.init(Cipher.ENCRYPT_MODE, key);

            while (inputLen - offSet > 0) {
                if (inputLen - offSet > 117) {
                    cache = cipher.doFinal(data, offSet, 117);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }

                out.write(cache, 0, cache.length);
                out.flush();
                i++;
                offSet = i * 117;
            }
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > 128) {
                    cache = cipher.doFinal(data, offSet, 128);
                    // 当最前面的数据是0，解密工具会错误的认为这是padding，因此导致长度不正确
                    if (cache.length < 117) {
                        byte[] temp = new byte[117];
                        System.arraycopy(cache, 0, temp, 117 - cache.length, cache.length);
                        cache = temp;
                    }
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                out.flush();
                i++;
                offSet = i * 128;
            }
        }
        byte[] result = out.toByteArray();
        out.close();
        return result;
    }
}