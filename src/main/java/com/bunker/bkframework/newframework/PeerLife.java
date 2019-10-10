package com.bunker.bkframework.newframework;

/**
 * 
 * Copyright 2016~ by bunker Corp.,
 * All rights reserved.
 *
 * @author Young soo Ahn <bunker.ys89@gmail.com>
 * 2016. 6. 27.
 *
 *
 */
public interface PeerLife {
	/**
	 */
	public void life();
	/**
	 */
	public boolean needRecycle();
	
	/**
	 */
	public boolean interceptCycle();
}
