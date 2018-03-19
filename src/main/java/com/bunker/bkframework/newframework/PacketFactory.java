package com.bunker.bkframework.newframework;

import java.io.OutputStream;

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

	public void logging(OutputStream output, PacketType pack);
	public void logging(OutputStream output, Packet<PacketType> pack);
}