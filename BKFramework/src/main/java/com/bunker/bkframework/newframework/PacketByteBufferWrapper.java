package com.bunker.bkframework.newframework;

import static com.bunker.bkframework.newframework.Constants.FLAG_LAST;
import static com.bunker.bkframework.newframework.Constants.FLAG_OFFSET;
import static com.bunker.bkframework.newframework.Constants.PACKET_TOTAL_SIZE;
import static com.bunker.bkframework.newframework.Constants.PAYLOAD_MAX;
import static com.bunker.bkframework.newframework.Constants.PAYLOAD_OFFSET;
import static com.bunker.bkframework.newframework.Constants.PAYLOAD_SIZE_OFFSET;
import static com.bunker.bkframework.newframework.Constants.SEQUENCE_OFFSET;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * 데이터를 패킷 이터레이터로만드는 클래스
 * 버퍼와 플레그의 재활용에 주목해야함
 * 
 * ----------------------
 * 주의사항
 * ByteBuffer 연산에 주의
 * ----------------------
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 6. 26.
 *
 *
 */
public class PacketByteBufferWrapper implements Iterator<ByteBuffer> {
	public ByteBuffer originData;
	private static final Object sequenceMutext = new Object();
	public short flag;
	public int currentSequence;
	public ByteBuffer buffer;
	volatile private static int sequanceFlow = 0;
	private final int originLimit;

	public PacketByteBufferWrapper(ByteBuffer data) {
		this(data, null);
	}

	public PacketByteBufferWrapper(ByteBuffer data, Integer sequence) {
		originLimit = data.limit();
		originData = data;
		if (data.limit() <= 0)
			flag += FLAG_LAST;

		if (sequence != null)
			currentSequence = sequence;
		else {
			synchronized (sequenceMutext) {
				currentSequence = sequanceFlow++;
			}
		}
	}

	@Override
	public boolean hasNext() {
		return (flag & FLAG_LAST) != FLAG_LAST;
	}

	@Override
	public ByteBuffer next() {
		buffer = ByteBuffer.allocateDirect(PACKET_TOTAL_SIZE);
		if (buffer.limit() < PACKET_TOTAL_SIZE) {
			flag += FLAG_LAST;
			return buffer;
		}
			
		int length = originLimit - originData.position();

		if (length > PAYLOAD_MAX) {
			length = PAYLOAD_MAX;
		} else
			flag += FLAG_LAST;

		originData.limit(originData.position() + length);
		buffer.position(PAYLOAD_OFFSET);
		buffer.put(originData);

		putShort(PAYLOAD_SIZE_OFFSET, (short) length);
		putShort(FLAG_OFFSET, (short) flag);
		putInt(SEQUENCE_OFFSET, currentSequence);

		buffer.position(buffer.capacity());
		buffer.flip();
		return buffer;
	}

	private void putShort(int index, short s) {
		buffer.putShort(index, s);
	}

	private void putInt(int index, int seq) {
		buffer.putInt(index, seq);
	}

	@Override
	public void remove() {
	}

	public int getTotlaPayloadSize() {
		return originLimit;
	}
}