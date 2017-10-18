package com.bunker.bkframework.sec;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SSLSecureFactory implements SecureFactory<ByteBuffer> {

	private SSLContext mContext;
	private String mUrl;
	private int mPort;
	private boolean mIsClient = false;


	/**
	 * Client
	 * @param keyStorePath
	 * @param keyPass
	 * @param trustPath
	 * @param trustPass
	 * @param url - 별로 안중요
	 * @param port - 별로 안중요
	 */
	public SSLSecureFactory(String keyStorePath, String keyPass, String trustPath, String trustPass, String url, int port) {
		this(keyStorePath, keyPass, trustPath, trustPass);
		mUrl = url;
		mPort = port;
		mIsClient = true;
	}

	/**
	 * Server
	 */
	public SSLSecureFactory(String keyStorePath, String keyPass, String trustPath, String trustPass) {
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			KeyStore ts;
			ts = KeyStore.getInstance("JKS");

			boolean keyExist = getClass().getClassLoader().getResource(keyStorePath) != null;
			boolean trustExist = getClass().getClassLoader().getResource(trustPath) != null;
			System.out.println("SSLSecureFactory:keyload classloader:" + keyExist + "," + "SSLSecureFactory:" + trustExist);

			if (keyExist) {
				ks.load(getClass().getClassLoader().getResourceAsStream(keyStorePath), keyPass.toCharArray());
			} else {
				File file = new File(keyStorePath);
				if (!file.exists()) {
					System.out.println("SSLSecureFactory:key file is not exist.");
					return;
				}
				
				FileInputStream fIn = new FileInputStream(file);
				ks.load(fIn, keyPass.toCharArray());
				System.out.println("SSLSecureFactory:keystore loaded.");
			}

			if (trustExist) {
				ts.load(getClass().getClassLoader().getResourceAsStream(trustPath), trustPass.toCharArray());
			} else {
				File file = new File(trustPath);
				if (!file.exists()) {
					System.out.println("SSLSecureFactory:trust file is not exist.");
					return;
				}
				
				FileInputStream fIn = new FileInputStream(file);
				ts.load(fIn, trustPass.toCharArray());				
				System.out.println("SSLSecureFactory:trust loaded.");
			}

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, keyPass.toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ts);

			mContext = SSLContext.getInstance("TLSv1.2");
			if (mIsClient)
				mContext.init(null, null, null);   
			else 
				mContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);   
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Secure<ByteBuffer> createSecure() {
		try {
			if (!mIsClient)
				return new SSLEngineAdapter(mContext);
			else
				return new SSLEngineAdapter(mUrl, mPort, mContext);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
