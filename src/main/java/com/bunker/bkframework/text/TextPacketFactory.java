package com.bunker.bkframework.text;

import java.io.OutputStream;

import com.bunker.bkframework.newframework.Packet;
import com.bunker.bkframework.newframework.PacketFactory;

public class TextPacketFactory implements PacketFactory<String> {

	@Override
	public Packet<String> creatPacket(String pack) {
		return new TextPacket(pack);
	}

	@Override
	public Packet<String> creatPacket(int size) {
		return new TextPacket(size);
	}

	@Override
	public void logging(OutputStream output, String pack) {
	}

	@Override
	public void logging(OutputStream output, Packet<String> pack) {
	}
}
