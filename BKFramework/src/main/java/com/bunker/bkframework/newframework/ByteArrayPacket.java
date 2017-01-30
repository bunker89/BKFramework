package com.bunker.bkframework.newframework;


import static com.bunker.bkframework.newframework.Constants.*; 
import java.nio.ByteBuffer;

public class ByteArrayPacket implements Packet<byte[]> {
	public boolean isFinal;
	public int sequence;
	public short payloadSize;
	public byte[] payload;
	public int lastIndex = 0;
	
	public ByteArrayPacket(short size) {
		payload = new byte[size];
		payloadSize = size;
	}

	public ByteArrayPacket(byte[] packet) {
		if ((packet[0] & FLAG_LAST) == FLAG_LAST)
			isFinal = true;
		ByteBuffer b = ByteBuffer.wrap(packet, PAYLOAD_SIZE_OFFSET, 2);
		payloadSize = b.getShort();
		payload = new byte[payloadSize];
		b = ByteBuffer.wrap(packet, SEQUENCE_OFFSET, 4);
		sequence = b.getInt();
		System.arraycopy(packet, PAYLOAD_OFFSET, payload, 0, payloadSize);
		lastIndex = payloadSize;
	}

	public byte[] getData() {
		return payload;
	}

	@Override
	public int getSize() {
		return payloadSize;
	}

	@Override
	public void putDataAtLast(byte []b) {
		System.arraycopy(b, 0, payload, lastIndex, b.length);
		lastIndex += b.length;
	}

	@Override
	public boolean isFinal() {
		return isFinal;
	}

	@Override
	public int getSequence() {
		return sequence;
	}
}