package com.shimizukenta.http;

public interface HttpServerService {
	
	/**
	 * 
	 * @param requestInfomation
	 * @return true if acceptable request
	 */
	public boolean accept(HttpRequestMessageInformation requestInfo);
	
	/**
	 * 
	 * @param writer
	 * @param requestInformation
	 * @param connectionValue
	 * @return true if Connection is Keep-Alive
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	public boolean tryService(
			HttpMessageWriter writer,
			HttpRequestMessageInformation requestInfo,
			HttpServerConnectionValue connectionValue)
					throws InterruptedException, HttpWriteException;
	
	
	public boolean addLogListener(HttpLogListener l);
	public boolean removeLogListener(HttpLogListener l);
	
	public boolean addResponseMessageLogListener(HttpResponseMessageLogListener l);
	public boolean removeResponseMessageLogListener(HttpResponseMessageLogListener l);
	
	public boolean addAccessLogListener(HttpAccessLogListener l);
	public boolean removeAccessLogListener(HttpAccessLogListener l);
	
}
