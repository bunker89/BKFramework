package com.bunker.bkframework.business;

public interface BusinessConnection<PacketType> {
	public void sendToBusiness(PacketType data, int sequence);
	public void establishedBusinessLogic();
	public void removePeerAtBusiness();
}
