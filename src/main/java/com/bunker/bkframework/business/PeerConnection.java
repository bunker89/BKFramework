package com.bunker.bkframework.business;

import java.util.Map;

public interface PeerConnection<SendDataType> {
	public void sendToPeer(SendDataType data, int sequence);
	public void closePeer();
	public Map<String, Object> getEnviroment();
}
