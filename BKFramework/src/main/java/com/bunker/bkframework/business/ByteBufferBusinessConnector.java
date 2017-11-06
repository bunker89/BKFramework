package com.bunker.bkframework.business;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 7. 7.
 *
 *
 */
public class ByteBufferBusinessConnector implements BusinessConnector<ByteBuffer> {
	private BusinessPeer<ByteBuffer> mPeer;
	private Business<ByteBuffer> mBusiness;
	private Map<String, Serializable> mEnviroment;

	public ByteBufferBusinessConnector(Business<ByteBuffer> business) {
		mBusiness = business;
	}

	@Override
	public void sendToBusiness(ByteBuffer data, int sequence) {
		byte []result = new byte[data.limit()];
		data.get(result, 0, data.limit());
		mBusiness.receive(this, result, sequence);
	}

	@Override
	public void sendToPeer(byte[] data, int sequence) {
		mPeer.write(ByteBuffer.wrap(data), sequence);
	}

	@Override
	public void closePeer() {
		mPeer.close();
	}

	public void removePeerAtBusiness() {
		mBusiness.removeBusinessData(this);
	}

	@Override
	public BusinessConnector<ByteBuffer> getInstance(BusinessPeer<ByteBuffer> peer)
			throws CloneNotSupportedException {
		ByteBufferBusinessConnector c = (ByteBufferBusinessConnector) clone();
		c.mPeer = peer;
		c.mEnviroment = new HashMap<String, Serializable>();
		return c;
	}

	@Override
	public void establishedBusinessLogic() {
		mBusiness.established(this);
	}

	@Override
	public Map<String, Serializable> getEnviroment() {
		return mEnviroment;
	}
}