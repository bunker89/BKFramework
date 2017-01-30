package com.bunker.bkframework.business;

public interface Business <PacketType> {
	public void established(PeerConnection b);
	public void receive(PeerConnection connector, byte[] data, int sequence);
	public void removeBusinessData(PeerConnection connector);
}