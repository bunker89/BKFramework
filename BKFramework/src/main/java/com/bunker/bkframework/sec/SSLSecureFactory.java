package com.bunker.bkframework.sec;

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
	}
	
	/**
	 * Server
	 */
	public SSLSecureFactory(String keyStorePath, String keyPass, String trustPath, String trustPass) {
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			KeyStore ts;
			ts = KeyStore.getInstance("JKS");

			ks.load(new FileInputStream(keyStorePath), keyPass.toCharArray());
			ts.load(new FileInputStream(trustPath), trustPass.toCharArray());

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

	@Override
	public Secure<ByteBuffer> createSecure() {
		try {
			if (mUrl != null)
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
