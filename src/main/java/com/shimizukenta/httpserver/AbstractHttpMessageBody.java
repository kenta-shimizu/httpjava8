package com.shimizukenta.httpserver;

public abstract class AbstractHttpMessageBody {
	
	private final byte[] body;
	private final int length;
	
	public AbstractHttpMessageBody(byte[] bs) {
		this.body = bs;
		this.length = bs.length;
	}
	
	public byte[] getBytes() {
		return body;
	}
	
	public int length() {
		return length;
	}
	
	@Override
	public String toString() {
		return "body-length: " + length;
	}

}
