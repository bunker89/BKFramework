package com.bunker.bkframework.newframework;

import java.nio.ByteBuffer;

public class ScalableByteBufferPacketFactory implements PacketFactory<ByteBuffer>{
	private int mPayloadSize = 0;

	@Override
	public Packet<ByteBuffer> creatPacket(ByteBuffer pack) {
		if (mPayloadSize == 0) {
			ByteBufferPacket packet = new ByteBufferPacket(pack);
			mPayloadSize = packet.getSize();
			return packet;
		}

		
//		if (pack.limit() - pack.position())
 		return null;
	}

	@Override
	public Packet<ByteBuffer> creatPacket(int size) {
		return null;
	}
}