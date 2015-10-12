package com.doing.team.util;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;

public class EncryptUtil {

    private static final String CHARSET_NAME = "UTF-8";

    // 不能修改
    private static final String DES = "DES";

    // 不能修改
    private static final String CBC_MODEL = "DES/CBC/PKCS5Padding";

    /**
     * DES加密
     *
     * @param plain 待加密字符串
     * @param key   DES密钥
     * @return 返回DES加密后byte[]
     */
    public static byte[] DesEncrypt(String plain, String key) {
        byte desBytes[] = null;
        if (plain != null && plain.length() > 0) {
            try {
                DESKeySpec dks = new DESKeySpec(key.getBytes(CHARSET_NAME));

                // 创建一个密匙工厂，然后用它把DESKeySpec转换成
                // 一个SecretKey对象
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
                SecretKey secretKey = keyFactory.generateSecret(dks);

                // using DES in CBC mode
                Cipher cipher = Cipher.getInstance(CBC_MODEL);

                // 初始化Cipher对象
                IvParameterSpec iv = new IvParameterSpec(key.getBytes(CHARSET_NAME));
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

                // 执行加密操作
                desBytes = cipher.doFinal(plain.getBytes(CHARSET_NAME));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return desBytes;
    }

    public static String DesDecrypt(byte[] desByteArray, String key) {
        String plain = null;
        if (desByteArray != null) {
            try {
                DESKeySpec dks = new DESKeySpec(key.getBytes(CHARSET_NAME));

                // 创建一个密匙工厂，然后用它把DESKeySpec转换成
                // 一个SecretKey对象
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
                SecretKey secretKey = keyFactory.generateSecret(dks);

                // using DES in CBC mode
                Cipher cipher = Cipher.getInstance(CBC_MODEL);

                // 初始化Cipher对象
                IvParameterSpec iv = new IvParameterSpec(key.getBytes(CHARSET_NAME));
                cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

                // 执行加密操作
                byte[] plainBytes = cipher.doFinal(desByteArray);

                plain = new String(plainBytes, CHARSET_NAME);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return plain;
    }

    public static byte[] AesEncrypt(String info, String key,String iv) {
        try {
            byte[] data = info.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec,ivParameterSpec);
            return cipher.doFinal(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String AesDecrypt(byte[] data, String key,String iv) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec,ivParameterSpec);
            byte[] decrypted = cipher.doFinal(data);
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String decrypt(String input, String key){
        byte[] output = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decode(input, Base64.DEFAULT));
        }catch(Exception e){
            System.out.println(e.toString());
        }
        return new String(output);
    }
}
