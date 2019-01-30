package com.edu.flinnt.core;

import com.edu.flinnt.util.LogWriter;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * class to generate MD5 string
 */
public class HTTPHelper {
	public static final String TAG = HTTPHelper.class.getSimpleName();

    /**
     * Generates MD5 String
     * @param str base string
     * @return MD5
     */
	public static String getMD5(String str) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(str.getBytes(), 0, str.length());
			String md5String = new BigInteger(1, m.digest()).toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while (md5String.length() < 32) {
				md5String = "0" + md5String;
			}
			return md5String;

		} catch (Exception e) {
			LogWriter.err(e);
		}
		return str;
	}
}
