package com.bunker.bkframework.business;

public interface BusinessConnection<RecieveDataType> {
	public void sendToBusiness(RecieveDataType data, int sequence);
	public void establishedBusinessLogic();
	public void removePeerAtBusiness();
}
