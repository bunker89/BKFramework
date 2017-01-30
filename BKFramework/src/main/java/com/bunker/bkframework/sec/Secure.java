package com.bunker.bkframework.sec;

import com.bunker.bkframework.newframework.PacketReceiver;
import com.bunker.bkframework.newframework.Writer;

/**
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 7. 5.
 *
 *
 */
public interface Secure <PacketType> extends Writer<PacketType>, PacketReceiver<PacketType> {
	public void startHandShaking(Writer<PacketType> w, PacketReceiver<PacketType> r, SecureCallback callback);
	public Secure<PacketType> unDecoSecure(Secure<PacketType> sec);
}