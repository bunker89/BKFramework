package com.bunker.bkframework.newframework;

import java.nio.ByteBuffer;

public class ByteBufferPayloadPacket implements Packet<ByteBuffer> {
	private ByteBuffer mPayloadBuffer;
	private boolean mIsFinal;
	private int mRemainSize;
	
	public ByteBufferPayloadPacket(ByteBuffer buffer, int remainSize) {
		mPayloadBuffer = buffer;
		mRemainSize = remainSize;
	}

	@Override
	public ByteBuffer getData() {
		return mPayloadBuffer;
	}

	@Override
	public int getSize() {
		return mPayloadBuffer.limit() - mPayloadBuffer.position();
	}

	@Override
	public boolean isFinal() {
		return mIsFinal;
	}

	@Override
	public boolean isFramworkPacket() {
		return false;
	}

	@Override
	public int getSequence() {
		return 0;
	}

	@Override
	public void putDataAtLast(ByteBuffer packet) {
		mPayloadBuffer.put(packet);
	}

	@Override
	public void putFrameworkPacketFlag(boolean isFramework) {		
	}
}
