package com.bunker.bkframework.newframework;

public interface PacketFactory <PacketType> {
	/**
	 * parse to Packet
	 * @param pack
	 * @return
	 */
	public Packet<PacketType> creatPacket(PacketType pack);
	/**
	 * create empty packet
	 * @param size
	 * @return
	 */
	public Packet<PacketType> creatPacket(int size);
}
