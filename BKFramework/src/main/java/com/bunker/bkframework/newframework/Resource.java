package com.bunker.bkframework.newframework;

public interface Resource <PacketType> {
	public Peer<PacketType> getPeer();
	public PacketType getReadBuffer();
	public void destroy();
}