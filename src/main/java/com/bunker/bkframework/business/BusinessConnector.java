package com.bunker.bkframework.business;

public interface BusinessConnector<PacketType, SendDataType, ReceiveDataType> extends Cloneable, PeerConnection<ReceiveDataType>, BusinessConnection<PacketType> {
	public BusinessConnector<PacketType, SendDataType, ReceiveDataType> getInstance(BusinessPeer<PacketType, SendDataType, ReceiveDataType>peer) throws CloneNotSupportedException;
}