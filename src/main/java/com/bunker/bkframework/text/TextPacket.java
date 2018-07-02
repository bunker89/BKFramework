package com.bunker.bkframework.text;

import com.bunker.bkframework.newframework.Packet;

public class TextPacket implements Packet<String> {
	private String mData;
	
	public TextPacket(String data) {
		mData = data;
	}
	
	public TextPacket(int size) {
		mData = "";
	}

	@Override
	public String getData() {
		return mData;
	}

	@Override
	public int getSize() {
		return mData.length();
	}

	@Override
	public boolean isFinal() {
		return true;
	}

	@Override
	public boolean isFramworkPacket() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSequence() {
		return 0;
	}

	@Override
	public void putDataAtLast(String packet) {
		mData += packet;
	}

	@Override
	public void putFrameworkPacketFlag(boolean isFramework) {
	}
}
