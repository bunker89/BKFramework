package com.bunker.bkframework.newframework;

public interface Writer<PacketType> {
	public void write(PacketType b, Integer sequence);
	public void write(PacketType b);
	public Writer<PacketType> unDecoWriter();	
	public void destroy();
}
