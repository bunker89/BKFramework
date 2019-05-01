package com.bunker.bkframework.newframework;

import static com.bunker.bkframework.newframework.Constants.*;

import java.util.Iterator;

/**
 * �����͸� ��Ŷ ���ͷ����ͷθ���� Ŭ����
 * ���ۿ� �÷����� ��Ȱ�뿡 �ָ��ؾ���
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 6. 26.
 *
 *
 */
public class PacketByteArrayWrapper implements Iterator<byte[]> {
	public byte []originData;

	private static final Object sequenceMutext = new Object();
	public short flag;
	public int sequance;
	public int currentIndex;
	public byte[] buffer = new byte[PACKET_DEFAULT_TOTAL_SIZE];
	private static int sequanceFlow = 0;
	private Integer reservedSequence;

	public PacketByteArrayWrapper(byte []data) {
		this(data, null);
	}

	public PacketByteArrayWrapper(byte []data, Integer sequence) {
		originData = data;
		if (data.length <= 0)
			flag += FLAG_LAST;
		putShort(SEQUENCE_OFFSET, flag);	
		reservedSequence = sequence;
	}

	@Override
	public boolean hasNext() {
		return (flag & FLAG_LAST) != FLAG_LAST;
	}

	@Override
	public byte[] next() {
		int length = originData.length - currentIndex;

		if (length > PAYLOAD_MAX) {
			length = PAYLOAD_MAX;
		}

		System.arraycopy(originData, currentIndex, buffer, PAYLOAD_OFFSET, length);

		currentIndex += length;

		if (currentIndex >= originData.length) {
			flag += FLAG_LAST;
		}
		putShort(PAYLOAD_SIZE_OFFSET, (short) length);
		putShort(FLAG_OFFSET, (short) flag);

		if (reservedSequence != null) { 
			synchronized (sequenceMutext) {
				putInt(SEQUENCE_OFFSET, reservedSequence++);
			}
		}
		else
			putInt(SEQUENCE_OFFSET, sequanceFlow++);

		return buffer;
	}

	private void putShort(int index, short s) {
		buffer[index] = (byte) ((s >> 8) & 0xff);
		buffer[index + 1] = (byte) (s & 0xff);
	}

	private void putInt(int index, int seq) {
		buffer[index] = (byte) (seq >> 24);
		buffer[index + 1] = (byte) ((seq >> 16) & 0xff);
		buffer[index + 2] = (byte) ((seq >> 8) & 0xff);
		buffer[index + 3] = (byte) (seq & 0xff);
	}
	
	@Override
	public void remove() {
	}
}