package com.bunker.bkframework.sec;

public interface SecureFactory<PacketType> {
	public Secure<PacketType> createSecure();
}
