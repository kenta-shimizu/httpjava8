package com.shimizukenta.http;

import java.util.Optional;

public abstract class AbstractHttpResponseMessageBuilderConfig {
	
	private String serverName;
	private long maxFileSize;
	private boolean addDateHeader;
	private boolean addLastModifiedHeader;
	
	public AbstractHttpResponseMessageBuilderConfig() {
		
		this.serverName = null;
		this.maxFileSize = (long)(Integer.MAX_VALUE);
		this.addDateHeader = true;
		this.addLastModifiedHeader = true;
	}
	
	public void serverName(CharSequence serverName) {
		synchronized ( this ) {
			this.serverName = serverName == null ? null : serverName.toString();
		}
	}
	
	public Optional<String> serverName() {
		synchronized ( this ) {
			return this.serverName == null ? Optional.empty() : Optional.of(this.serverName);
		}
	}
	
	public long maxFileSize() {
		synchronized ( this ) {
			return this.maxFileSize;
		}
	}
	
	public void maxFileSize(long size) {
		synchronized ( this ) {
			this.maxFileSize = size;
		}
	}
	
	public boolean addDateHeader() {
		synchronized ( this ) {
			return this.addDateHeader;
		}
	}
	
	public void addDateHeader(boolean f) {
		synchronized ( this ) {
			this.addDateHeader = f;
		}
	}
	
	public boolean addLastModifiedHeader() {
		synchronized ( this ) {
			return this.addLastModifiedHeader;
		}
	}
	
	public void addLastModifiedHader(boolean f) {
		synchronized ( this ) {
			this.addLastModifiedHeader = f;
		}
	}
	
}
