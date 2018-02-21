package com.bunker.bkframework.business;

public interface BusinessConnector<PacketType> extends Cloneable, PeerConnection, BusinessConnection<PacketType> {
	public BusinessConnector<PacketType> getInstance(BusinessPeer<PacketType>peer) throws CloneNotSupportedException;
}