package com.bunker.bkframework.sec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

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
		mUrl = url;
		mPort = port;
		mIsClient = true;
		
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			boolean keyExist = getClass().getClassLoader().getResource(keyStorePath) != null;
			keyLoad(keyExist, ks, keyStorePath, keyPass);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, keyPass.toCharArray());

			KeyStore ts = KeyStore.getInstance("JKS");
			boolean trustExist = getClass().getClassLoader().getResource(trustPath) != null;
			keyLoad(trustExist, ts, trustPath, trustPass);
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ts);

			mContext = SSLContext.getInstance("TLSv1.2");
			mContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException | UnrecoverableKeyException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Server
	 */
	public SSLSecureFactory(String keyStorePath, String keyPass, String trustPath, String trustPass) {
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			KeyStore ts = KeyStore.getInstance("JKS");

			boolean keyExist = getClass().getClassLoader().getResource(keyStorePath) != null;
			boolean trustExist = getClass().getClassLoader().getResource(trustPath) != null;

			keyLoad(keyExist, ks, keyStorePath, keyPass);
			keyLoad(trustExist, ts, trustPath, trustPass);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, keyPass.toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ts);

			mContext = SSLContext.getInstance("TLSv1.2");
			mContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);   
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void keyLoad(boolean classPathExist, KeyStore ks, String keyStorePath, String keyPass) throws NoSuchAlgorithmException, CertificateException, IOException {
		if (classPathExist) {
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
	}

	@Override
	public SSLEngineAdapter createSecure() {
		try {
			if (mIsClient)
				return new SSLEngineAdapter(mUrl, mPort, mContext);
			else
				return new SSLEngineAdapter(mContext);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
