package com.bunker.bkframework.newframework;

public class Constants {
	public static final int PACKET_TOTAL_SIZE = 1024;
	public static final short FLAG_LAST = 0b0000000000000001;
	public static final int FLAG_OFFSET = 0;
	public static final int SEQUENCE_OFFSET = 2;
	public static final int PAYLOAD_SIZE_OFFSET = 6;
	public static final int PAYLOAD_OFFSET = 8;
	public static final int PAYLOAD_MAX = PACKET_TOTAL_SIZE - 8;
}