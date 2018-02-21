package com.bunker.bkframework.business;

import java.util.Map;

public interface PeerConnection {
	public void sendToPeer(byte []data, int sequence);
	public void closePeer();
	public Map<String, Object> getEnviroment();
}
