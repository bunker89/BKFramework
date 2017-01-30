package com.bunker.bkframework.newframework;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BufferManager {
	/**
	 * 0 ->32
	 * 1 ->128
	 * 2 ->512
	 * 3 ->1024
	 * 4 ->2048
	 * 5 ->4096
	 */
	private static final int orderList[] = {
			32,
			128,
			512,
			1024,
			2048,
			4096
	};

	private static int bufferStock[] = {
			10,
			5,
			4,
			3,
			2,
			2
	};

	private static Map<Integer, Integer> orderHash = new HashMap<Integer, Integer>() {
		{
			put(32, 0);
			put(128, 1);
			put(512, 2);
			put(1024, 3);
			put(2048, 4);
			put(4096, 5);
		}
	};

	private static ArrayList<byte[]> []bufferList = new ArrayList[] {
		new ArrayList<byte[]>(),
		new ArrayList<byte[]>(),
		new ArrayList<byte[]>(),
		new ArrayList<byte[]>(),
		new ArrayList<byte[]>(),
		new ArrayList<byte[]>(),
	};
	private static Object bufferMutex = new Object();

	public static BufferManager manager = null;


	public static byte[] getBuffer() {
		return getBuffer(3);
	}

	public static byte[] getBuffer(int order) {
		byte[] buffer = null;
		synchronized (bufferMutex) {
			if (!bufferList[order].isEmpty()) {
				buffer = bufferList[order].get(0);
			} else {
				return new byte[orderList[order]];
			}
		}
		return buffer;
	}

	public static void recycleBuffer(byte[] buffer) {
		if (!orderHash.containsKey(buffer.length))
			return;
		synchronized (bufferMutex) {
			int order = (int) orderHash.get(buffer.length);
			if (bufferList[order].size() < bufferStock[order])
				bufferList[order].add(buffer);		
		}
	}
	
	public static ByteBuffer allocByteBuffer(int size) {
		return ByteBuffer.allocate(size);
	}
}