package com.bunker.bkframework.newframework;

import java.nio.ByteBuffer;

public class FixedSizeByteBufferPacketFactory implements PacketFactory<ByteBuffer> {
	@Override
	public Packet<ByteBuffer> creatPacket(ByteBuffer pack) {
		return new ByteBufferPacket(pack);
	}

	@Override
	public Packet<ByteBuffer> creatPacket(int size) {
		return new ByteBufferPacket(size);
	}
}