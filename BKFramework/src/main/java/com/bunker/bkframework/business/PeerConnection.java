package com.bunker.bkframework.business;

import java.io.Serializable;
import java.util.Map;

public interface PeerConnection {
	public void sendToPeer(byte []data, int sequence);
	public void closePeer();
	public Map<String, Serializable> getEnviroment();
}
