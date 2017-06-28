package com.bunker.bkframework.newframework;

public interface Packet <PacketType> {
	public PacketType getData();
	public int getSize();
	public boolean isFinal();
	public boolean isFramworkPacket();
	public int getSequence();
	public void putDataAtLast(PacketType packet);
	public void putFrameworkPacketFlag(boolean isFramework);
}
