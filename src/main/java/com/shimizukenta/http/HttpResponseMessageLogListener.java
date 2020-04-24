package com.shimizukenta.http;

import java.util.EventListener;

public interface HttpResponseMessageLogListener extends EventListener {
	public void receive(HttpResponseMessageLog log);
}
