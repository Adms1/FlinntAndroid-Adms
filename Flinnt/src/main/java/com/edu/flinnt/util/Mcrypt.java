package com.edu.flinnt.util;


import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class to manage encryption and decryption
 */
public class Mcrypt {

    //   private String iv = "fedcba9876543210";//Dummy iv (CHANGE IT!)
    private final String iv = "0a1b2c3d4e5f6789";//Dummy iv (CHANGE IT!)
    private final IvParameterSpec ivspec;
    private final SecretKeySpec keyspec;
    private Cipher cipher;

    // private String SecretKey = "0123456789abcdef";//Dummy secretKey (CHANGE IT!)
    private final String SecretKey = "flinnt0123456789";//Dummy secretKey (CHANGE IT!)

    //mcrypt = new MCrypt();
      /* Encrypt */
    //  String encrypted = MCrypt.bytesToHex( mcrypt.encrypt("Text to Encrypt") );
      /* Decrypt */
    //   String decrypted = new String( mcrypt.decrypt( encrypted ) );

    public Mcrypt() {
        this.ivspec = new IvParameterSpec(this.iv.getBytes());

        this.keyspec = new SecretKeySpec(this.SecretKey.getBytes(), "AES");

        try {
            this.cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            LogWriter.err(e);
        } catch (NoSuchPaddingException e) {
            LogWriter.err(e);
        }
    }

    public byte[] encrypt(String text) throws Exception {
        if (text == null || text.length() == 0)
            throw new Exception("Empty string");

        byte[] encrypted = null;

        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.keyspec, this.ivspec);

            encrypted = this.cipher.doFinal(Mcrypt.padString(text).getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    public byte[] decrypt(String code) throws Exception {
        if (code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted = null;

        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.keyspec, this.ivspec);

            decrypted = this.cipher.doFinal(Mcrypt.hexToBytes(code));
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }


    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }

        int len = data.length;
        String str = "";
        for (int i = 0; i < len; i++) {
            if ((data[i] & 0xFF) < 16)
                str = str + "0" + Integer.toHexString(data[i] & 0xFF);
            else
                str = str + Integer.toHexString(data[i] & 0xFF);
        }
        return str;
    }


    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }


    private static String padString(String source) {
        char paddingChar = ' ';
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++) {
            source += paddingChar;
        }

        return source;
    }
}


