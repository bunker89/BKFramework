package com.bunker.bkframework.text;

import java.util.HashMap;
import java.util.Map;

import com.bunker.bkframework.business.Business;
import com.bunker.bkframework.business.BusinessConnector;
import com.bunker.bkframework.business.BusinessPeer;

public class TextBusinessConnector implements BusinessConnector<String, String, String> {
	private BusinessPeer<String, String, String> mPeer;
	private Business<String, String, String> mBusiness;
	private Map<String, Object> mEnviroment;

	public TextBusinessConnector(Business<String, String, String> business) {
		mBusiness = business;
	}

	@Override
	public void sendToBusiness(String data, int sequence) {
		mBusiness.receive(this, data, sequence);
	}

	@Override
	public void sendToPeer(String data, int sequence) {
		mPeer.write(data, sequence);
	}

	@Override
	public void closePeer() {
		mPeer.close();
	}

	public void removePeerAtBusiness() {
		mBusiness.removeBusinessData(this);
	}

	@Override
	public BusinessConnector<String, String, String> getInstance(BusinessPeer<String, String, String> peer)
			{
		TextBusinessConnector c = null;
		try {
			c = (TextBusinessConnector) clone();
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
