package com.shimizukenta.httpserver;

import java.util.EventListener;

public interface HttpLogListener extends EventListener {
	public void receive(HttpLog log);
}
