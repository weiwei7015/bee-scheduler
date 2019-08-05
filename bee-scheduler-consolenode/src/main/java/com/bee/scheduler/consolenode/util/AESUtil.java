package com.bee.scheduler.consolenode.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author weiwei
 */
public class AESUtil {

    private static final String KEY_AES = "AES";
    private static final String S_KEY = "1829367483762546";

    public static String encrypt(String src) throws Exception {
//        if (key == null || key.length() != 16) {
//            throw new Exception("key不满足条件");
//        }
        byte[] raw = S_KEY.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(src.getBytes());
        return byte2hex(encrypted);
    }

    public static String decrypt(String src) throws Exception {
//        if (key == null || key.length() != 16) {
//            throw new Exception("key不满足条件");
//        }
        byte[] raw = S_KEY.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] encrypted1 = hex2byte(src);
        byte[] original = cipher.doFinal(encrypted1);
        return new String(original);
    }

    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }

    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (byte value : b) {
            stmp = (Integer.toHexString(value & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toUpperCase();
    }
}