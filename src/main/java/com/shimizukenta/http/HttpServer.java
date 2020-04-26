package com.shimizukenta.http;

import java.io.Closeable;
import java.io.IOException;

public interface HttpServer extends Closeable {
	
	public void open() throws IOException;
	
	public boolean addServerService(HttpServerService s);
	public boolean removeServerService(HttpServerService s);
	
	public boolean addLogListener(HttpLogListener l);
	public boolean removeLogListener(HttpLogListener l);
	
	public boolean addRequestMessageLogListener(HttpRequestMessageLogListener l);
	public boolean removeRequestMessageLogListener(HttpRequestMessageLogListener l);
	
	public boolean addResponseMessageLogListener(HttpResponseMessageLogListener l);
	public boolean removeResponseMessageLogListener(HttpResponseMessageLogListener l);
	
	public boolean addAccessLogListener(HttpAccessLogListener l);
	public boolean removeAccessLogListener(HttpAccessLogListener l);
	
}
