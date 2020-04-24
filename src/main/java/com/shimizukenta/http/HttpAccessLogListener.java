package com.shimizukenta.http;

import java.util.EventListener;

public interface HttpAccessLogListener extends EventListener {
	public void receive(HttpAccessLog log);
}
