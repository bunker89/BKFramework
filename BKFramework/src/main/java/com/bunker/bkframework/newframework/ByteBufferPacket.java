package com.bunker.bkframework.newframework;

import static com.bunker.bkframework.newframework.Constants.FLAG_LAST;
import static com.bunker.bkframework.newframework.Constants.PAYLOAD_OFFSET;
import static com.bunker.bkframework.newframework.Constants.PAYLOAD_SIZE_OFFSET;
import static com.bunker.bkframework.newframework.Constants.SEQUENCE_OFFSET;

import java.nio.ByteBuffer;

public class ByteBufferPacket implements Packet<ByteBuffer>{
	public boolean isFinal;
	public int sequence;
	public int payloadSize;
	public ByteBuffer payloadBuffer;

	public ByteBufferPacket(int size) {
		payloadBuffer = ByteBuffer.allocate(size);
		payloadSize = size;
	}

	public ByteBufferPacket(ByteBuffer packet) {
		if ((packet.getShort(0) & FLAG_LAST) == FLAG_LAST)
			isFinal = true;
		payloadSize = packet.getShort(PAYLOAD_SIZE_OFFSET);
		sequence = packet.getInt(SEQUENCE_OFFSET);
		payloadBuffer = ByteBuffer.allocate(payloadSize);
		packet.position(PAYLOAD_OFFSET);
		packet.limit(PAYLOAD_OFFSET + payloadSize);
		payloadBuffer.put(packet);
		payloadBuffer.position(payloadSize);
	}

	@Override
	public ByteBuffer getData() {
		ByteBuffer temp = payloadBuffer.duplicate();
		temp.flip();
		return temp;
	}

	@Override
	public int getSize() {
		return payloadSize;
	}

	@Override
	public boolean isFinal() {
		return isFinal;
	}

	@Override
	public int getSequence() {
		return sequence;
	}

	@Override
	public void putDataAtLast(ByteBuffer packet) {
		payloadBuffer.put(packet);		
	}
}
