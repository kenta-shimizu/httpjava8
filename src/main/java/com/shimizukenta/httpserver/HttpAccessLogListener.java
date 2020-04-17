package com.shimizukenta.httpserver;

import java.util.EventListener;

public interface HttpAccessLogListener extends EventListener {
	public void receive(HttpAccessLog log);
}
