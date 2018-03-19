package com.bunker.bkframework.newframework;

import java.io.OutputStream;
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

	@Override
	public void logging(OutputStream output, ByteBuffer pack) {
		
	}

	@Override
	public void logging(OutputStream output, Packet<ByteBuffer> pack) {
	}
}