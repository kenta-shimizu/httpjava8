package com.shimizukenta.httpserver;

public interface HttpServerService {
	
	/**
	 * 
	 * @param request
	 * @return true if acceptable request
	 */
	public boolean accept(HttpRequestMessage request);
	
	/**
	 * 
	 * @param writer
	 * @param request
	 * @param connectionValue
	 * @return true if Connection is Keep-Alive
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	public boolean tryService(
			HttpMessageWriter writer,
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue)
					throws InterruptedException, HttpWriteException;
	
	
	public boolean addLogListener(HttpLogListener l);
	public boolean removeLogListener(HttpLogListener l);
	
	public boolean addResponseMessageLogListener(HttpResponseMessageLogListener l);
	public boolean removeResponseMessageLogListener(HttpResponseMessageLogListener l);
	
	public boolean addAccessLogListener(HttpAccessLogListener l);
	public boolean removeAccessLogListener(HttpAccessLogListener l);
	
}
