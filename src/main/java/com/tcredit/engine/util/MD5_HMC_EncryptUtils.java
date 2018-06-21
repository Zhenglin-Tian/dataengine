package com.tcredit.engine.util;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * Created by ZL.T on 2017/1/9.
 */
public class MD5_HMC_EncryptUtils {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(MD5_HMC_EncryptUtils.class);

    private static final String MD5 = "MD5";
    private static final String HMC_SHA1 = "HMACSHA1"; //HmacSHA1
    private static final String HMC_MD5 = "HmacMD5";
    private static final String CHARSET_UTF8 = "UTF-8";


    /**
     * @param rawResource
     * @param n
     * @return
     */
    public static String getMd5(String rawResource, int n) {
        byte[] bytes = generatingMD5Coder(rawResource, n);
        return byte2hex(bytes);
    }


    /**
     * MD5加密
     *
     * @param rawResource
     * @param n      加密次数
     * @return
     */
    public static byte[] generatingMD5Coder(String rawResource, int n) {
        byte[] bytes=null;
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            bytes = digest.digest(rawResource.getBytes(CHARSET_UTF8));
            for (int i = 0; i < n - 1; i++) {
                digest.reset();
                bytes = digest.digest(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }


    /**
     * HMACSHA1 是从 SHA1 哈希函数构造的一种键控哈希算法，被用作 HMAC（基于哈希的消息验证代码）。
     * 此 HMAC 进程将密钥与消息数据混合，使用哈希函数对混合结果进行哈希计算，
     * 将所得哈希值与该密钥混合，然后再次应用哈希函数。 输出的哈希值长度为 160 位。
     * 在发送方和接收方共享机密密钥的前提下，HMAC 可用于确定通过不安全信道发送的消息是否已被篡改。
     * 发送方计算原始数据的哈希值，并将原始数据和哈希值放在一个消息中同时传送。
     * 接收方重新计算所接收消息的哈希值，并检查计算所得的 HMAC 是否与传送的 HMAC 匹配。
     * <p>
     * 因为更改消息和重新生成正确的哈希值需要密钥，所以对数据或哈希值的任何更改都会导致不匹配。
     * 因此，如果原始的哈希值与计算得出的哈希值相匹配，则消息通过身份验证。
     * <p>
     * SHA-1（安全哈希算法，也称为 SHS、安全哈希标准）是由美国政府发布的一种加密哈希算法。
     * 它将从任意长度的字符串生成 160 位的哈希值。
     * HMACSHA1 接受任何大小的密钥，并产生长度为 160 位的哈希序列。
     */

    /**
     *
     * @param rawResource
     * @param secret
     * @return
     */
    public static String getHmc(String rawResource,String secret){
        try {
            byte[] bytes = encryptHMAC(rawResource, secret);
            return byte2hex(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * HMC_SHA1加密
     *
     * @param data
     * @param secret
     * @return
     * @throws Exception
     */
    public static byte[] encryptHMAC(String data, String secret){
        byte[] bytes = null;
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(CHARSET_UTF8), HMC_SHA1);
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data.getBytes(CHARSET_UTF8));
        } catch (Exception gse) {
            LOGGER.error("数据：{}，加密出错",data,gse);
        }
        return bytes;
    }

    /**
     * 将字节数组转换为字符串
     *
     * @param bytes
     * @return
     */
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex);
        }
        return sign.toString();
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        /*String aaaa = getMd5("aaaa", 1);
        System.out.println(aaaa);*/
        System.out.println("==============");
        String aaaa = getHmc("aaaa", "123456");
        System.out.println(aaaa);
        System.out.println(aaaa.length());

    }
}
