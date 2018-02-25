package com.bunker.bkframework.business;

public interface Business <PacketType, SendDataType, RecieveDataType> {
	public void established(PeerConnection<SendDataType> b);
	public void receive(PeerConnection<SendDataType> connector, RecieveDataType data, int sequence);
	public void removeBusinessData(PeerConnection<SendDataType> connector);
}