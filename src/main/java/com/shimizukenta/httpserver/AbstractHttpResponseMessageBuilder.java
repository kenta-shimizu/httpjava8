package com.shimizukenta.httpserver;

public abstract class AbstractHttpResponseMessageBuilder implements HttpResponseMessageBuilder {
	
	private final AbstractHttpResponseMessageBuilderConfig config;
	
	public AbstractHttpResponseMessageBuilder(AbstractHttpResponseMessageBuilderConfig config) {
		this.config = config;
	}
	
	/**
	 * Prototype-Method<br />
	 * 
	 * @return Http-Version
	 */
	abstract protected HttpVersion version();

	@Override
	public HttpResponseMessage build(
			HttpStatus status,
			HttpMessageHeaderGroup headerGroup,
			AbstractHttpMessageBody body) {
		
		return new HttpResponseMessage(
				HttpMessageStatusLine.create(version(), status),
				headerGroup,
				body);
	}
	
	@Override
	public HttpResponseMessage buildJson(HttpRequestMessage request, byte[] json) {
		
		//TODO
		
		return null;
	}
	
	@Override
	public HttpResponseMessage buildXml(HttpRequestMessage request, byte[] xml) {
		
		//TODO
		
		return null;
	}
	
}
