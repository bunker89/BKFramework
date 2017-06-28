package com.bunker.bkframework.newframework;

import static com.bunker.bkframework.newframework.Constants.*; 
import java.nio.ByteBuffer;

public class ByteArrayPacket implements Packet<byte[]> {
	public boolean mIsFinal;
	public boolean mIsFrameworkPacket = false;
	public int mSequence;
	public short mPayloadSize;
	public byte[] mPayload;
	public int mLastIndex = 0;
	
	public ByteArrayPacket(short size) {
		mPayload = new byte[size];
		mPayloadSize = size;
	}

	public ByteArrayPacket(byte[] packet) {
		if ((packet[0] & FLAG_LAST) == FLAG_LAST)
			mIsFinal = true;
		if ((packet[0] & FLAG_FRAMEWORK_PACKET) == FLAG_FRAMEWORK_PACKET)
			mIsFrameworkPacket = true;
		ByteBuffer b = ByteBuffer.wrap(packet, PAYLOAD_SIZE_OFFSET, 2);
		mPayloadSize = b.getShort();
		mPayload = new byte[mPayloadSize];
		b = ByteBuffer.wrap(packet, SEQUENCE_OFFSET, 4);
		mSequence = b.getInt();
		System.arraycopy(packet, PAYLOAD_OFFSET, mPayload, 0, mPayloadSize);
		mLastIndex = mPayloadSize;
	}

	public byte[] getData() {
		return mPayload;
	}

	@Override
	public int getSize() {
		return mPayloadSize;
	}

	@Override
	public void putDataAtLast(byte []b) {
		System.arraycopy(b, 0, mPayload, mLastIndex, b.length);
		mLastIndex += b.length;
	}

	@Override
	public boolean isFinal() {
		return mIsFinal;
	}

	@Override
	public int getSequence() {
		return mSequence;
	}

	@Override
	public boolean isFramworkPacket() {
		return mIsFrameworkPacket;
	}

	@Override
	public void putFrameworkPacketFlag(boolean isFramework) {
		mIsFrameworkPacket = isFramework;
	}
}