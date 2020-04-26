package com.shimizukenta.http;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class HttpVersion1p1AsynchronousSocketChannelServerConfig {
	
	public static final long defaultKeepAliveTimeout = 5;
	public static final int defaultKeepAliveMax = 100;
	
	private final Collection<SocketAddress> binds = new CopyOnWriteArrayList<>();
	private long keepALiveTimeout;
	private int keepALiveMax;
	
	public HttpVersion1p1AsynchronousSocketChannelServerConfig() {
		this.keepALiveTimeout = defaultKeepAliveTimeout;
		this.keepALiveMax = defaultKeepAliveMax;
	}
	
	
	public boolean addBind(SocketAddress s) {
		return binds.add(s);
	}
	
	public boolean removeBind(SocketAddress s) {
		return binds.remove(s);
	}
	
	public Collection<SocketAddress> binds() {
		return Collections.unmodifiableCollection(binds);
	}
	
	
	public void keepAliveTimeout(long t) {
		synchronized ( this ) {
			this.keepALiveTimeout = t;
		}
	}
	
	public long keepAliveTimeout() {
		synchronized ( this ) {
			return this.keepALiveTimeout;
		}
	}
	
	
	public void keepAliveMax(int v) {
		synchronized ( this ) {
			this.keepALiveMax = v;
		}
	}
	
	public int keepAliveMax() {
		synchronized ( this ) {
			return this.keepALiveMax;
		}
	}
	
}
