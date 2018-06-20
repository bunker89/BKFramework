package com.bunker.bkframework.newframework;

public interface PacketReceiver<PacketType> {
	public void decodePacket(PacketType packet, int sequence);
	public PacketReceiver<PacketType> unDecoReceiver();
}