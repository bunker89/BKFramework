package com.bunker.bkframework.tool;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordTool {
	private final String _Log = "PasswordTool";

	private final char[] DIGITS
	= {'0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	public String toHex(byte[] data) {
		final StringBuffer sb = new StringBuffer(data.length * 2);
		for (int i = 0; i < data.length; i++) {
			sb.append(DIGITS[(data[i] >>> 4) & 0x0F]);
			sb.append(DIGITS[data[i] & 0x0F]);
		}
		return sb.toString();
	}

	private String inspectBytes(byte []sec) {
		return toHex(sec);
	}

	public String createRandomToken() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		return inspectBytes(salt);
	}

	public String getFinalPassword(String decodedPassword, String salt) {
		try {
			return getFinalPassword(decodedPassword.getBytes("UTF-8"), salt);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getFinalPassword(byte []decodedPassword, String salt) {
		MessageDigest digest;
		byte[] encodingPassword = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			digest.update(salt.getBytes("UTF-8"));
			encodingPassword = digest.digest(decodedPassword);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return inspectBytes(encodingPassword);
	}
}