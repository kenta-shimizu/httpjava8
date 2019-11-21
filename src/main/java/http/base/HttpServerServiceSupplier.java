package http.base;

public interface HttpServerServiceSupplier {
	
	/**
	 * 
	 * @param request
	 * @return true if request acceptable
	 * @throws HttpMessageParseException
	 */
	public boolean accept(HttpRequestMessage request)
			throws HttpMessageParseException;
	
	/**
	 * 
	 * @param writer
	 * @param request
	 * @param connectionValue
	 * @return true if KeepAlive
	 * @throws InterruptedException
	 * @throws HttpWriteMessageException
	 * @throws HttpMessageParseException
	 */
	public boolean tryService(HttpMessageWriter writer, HttpRequestMessage request, HttpServerConnectionValue connectionValue)
			throws InterruptedException, HttpWriteMessageException, HttpMessageParseException;
	
}
