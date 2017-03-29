package com.bunker.bkframework.tool;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

public class PasswordTool {
	private final String _Log = "PasswordTool";

	private String inspectBytes(byte []sec) {
		return new String(Hex.encodeHex(sec, false));
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