package com.bunker.bkframework.nio;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.business.BusinessConnector;
import com.bunker.bkframework.business.BusinessPeer;

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
public class ByteBufferBusinessConnector implements BusinessConnector<ByteBuffer, byte[], byte []> {
	private BusinessPeer<ByteBuffer, byte[], byte[]> mPeer;
	private Business<ByteBuffer, byte[], byte[]> mBusiness;
	private Map<String, Object> mEnviroment;

	public ByteBufferBusinessConnector(Business<ByteBuffer, byte[], byte[]> business) {
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
	public BusinessConnector<ByteBuffer, byte[], byte[]> getInstance(BusinessPeer<ByteBuffer, byte[], byte[]> peer)
			{
		ByteBufferBusinessConnector c = null;
		try {
			c = (ByteBufferBusinessConnector) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		c.mPeer = peer;
		c.mEnviroment = new HashMap<String, Object>();
		return c;
	}

	@Override
	public void establishedBusinessLogic() {
		mBusiness.established(this);
	}

	@Override
	public Map<String, Object> getEnviroment() {
		return mEnviroment;
	}
}