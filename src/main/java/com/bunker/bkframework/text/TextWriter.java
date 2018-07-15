package com.bunker.bkframework.text;

import com.bunker.bkframework.newframework.Writer;

public class TextWriter implements Writer<String> {
	private String mResult;

	@Override
	public void write(String b, Integer sequence) {
		write(b);
	}

	@Override
	public void write(String b) {
		if (mResult != null) {
			mResult += b;
		} else { 
			mResult = b;
		}
	}

	@Override
	public Writer<String> unDecoWriter() {
		return this;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void setWriteBufferSize(int size) {
	}

	public String getResult() {
		String ret = mResult;
		mResult = null;
		return ret;
	}
}
