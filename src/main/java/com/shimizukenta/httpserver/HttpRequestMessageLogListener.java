package com.shimizukenta.httpserver;

import java.util.EventListener;

public interface HttpRequestMessageLogListener extends EventListener {
	public void receive(HttpRequestMessageLog log);
}
