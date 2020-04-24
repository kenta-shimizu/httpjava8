package com.shimizukenta.http;

import java.util.EventListener;

public interface HttpRequestMessageLogListener extends EventListener {
	public void receive(HttpRequestMessageLog log);
}
