package com.bunker.bkframework.newframework;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * NIO용 Writer
 * 
 * ----------------------
 * 주의사항
 * 이 클래스에서는 절대 flip, clear 등의 바이트버퍼와 관련된 연산을 하지 않는다. (Buffer가 꼬이는것을 방지하기 위해서)
 * ----------------------
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 7. 5.
 *
 *
 */
public class NIOWriter implements Writer<ByteBuffer> {
	private SocketChannel mChannel;
	private Peer<ByteBuffer> mPeer;
	private final static String _TAG = "NIOWriter";

	public NIOWriter(SocketChannel channel, Peer<ByteBuffer> peer) {
		mChannel = channel;
		mPeer = peer;
	}

	@Override
	public void write(ByteBuffer packet, Integer sequence) {
		write(new PacketByteBufferWrapper(packet, sequence));
	}

	@Override
	public void write(ByteBuffer packet) {
		write(new PacketByteBufferWrapper(packet));
	}

	private void write(PacketByteBufferWrapper wrapper) {
		while (wrapper.hasNext()) {
//			System.out.println(wrapper.next());
			ByteBuffer buffer = wrapper.next();
			if (buffer == null || buffer.position() != 0 || buffer.limit() != Constants.PACKET_DEFAULT_TOTAL_SIZE)
				System.out.println("err");
			try {
				if (buffer.limit() != Constants.PACKET_DEFAULT_TOTAL_SIZE) {
					Logger.err(_TAG, "Write packet size error");
					return;
				}

				if (mChannel.write(buffer) <= 0) {
					Logger.err(_TAG, "NIO Write buffer overflow!");
				}
			} catch (IOException e) {
				close();
				Logger.logging(_TAG, "Connection destroy suddenly");
			}
		}
		/*
		if (c != 10)
			System.out.println(c);
			*/
	}

	@Override
	public Writer<ByteBuffer> unDecoWriter() {
//		System.out.println(count++);
		return this;
	}

	private void close() {
		if (mPeer != null)
			mPeer.close();
		mPeer = null;
	}

	@Override
	public void destroy() {
		try {
			mChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setWriteBufferSize(int size) {
		try {
			mChannel.socket().setSendBufferSize(size);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}