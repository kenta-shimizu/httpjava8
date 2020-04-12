package com.shimizukenta.http;

import java.util.EventListener;

public interface HttpLogListener extends EventListener {
	public void receive(HttpLog log);
}
