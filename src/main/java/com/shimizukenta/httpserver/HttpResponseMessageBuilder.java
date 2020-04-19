package com.shimizukenta.httpserver;

import java.nio.charset.StandardCharsets;

public interface HttpResponseMessageBuilder {
	
	default HttpResponseMessage build(
			HttpVersion version,
			HttpStatus status,
			HttpMessageHeaderGroup headerGroup,
			AbstractHttpMessageBody body) {
		
		return new HttpResponseMessage(
				HttpMessageStatusLine.create(version, status),
				headerGroup,
				body);
	}
	
	default public HttpResponseMessage build(HttpStatus status) {
		return build(status, HttpMessageHeaderGroup.empty(), HttpMessageBody.empty());
	}
	
	default public HttpResponseMessage build(HttpStatus status, HttpMessageHeaderGroup headerGroup) {
		return build(status, headerGroup, HttpMessageBody.empty());
	}
	
	public HttpResponseMessage build(HttpStatus status, HttpMessageHeaderGroup headerGroup, AbstractHttpMessageBody body);
	
	/**
	 * build Response Message<br />
	 * 
	 * @param Request-Message
	 * @param json
	 * @return Response-Message
	 */
	public HttpResponseMessage buildJson(HttpRequestMessage request, byte[] json);
	
	/**
	 * build Response Message<br />
	 * parse UTF-8<br />
	 * 
	 * @param Request-Message
	 * @param json
	 * @return Response-Message
	 */
	default public HttpResponseMessage buildJson(HttpRequestMessage request, CharSequence json) {
		return buildJson(request, json);
	}
	
	/**
	 * build Response Message<br />
	 * 
	 * @param Request-Message
	 * @param xml
	 * @return Response-Message
	 */
	public HttpResponseMessage buildXml(HttpRequestMessage request, byte[] xml);
	
	/**
	 * build Response Message<br />
	 * parse UTF-8<br />
	 * 
	 * @param Request-Message
	 * @param xml
	 * @return Response-Message
	 */
	default public HttpResponseMessage buildXml(HttpRequestMessage request, CharSequence xml) {
		return buildXml(request, xml.toString().getBytes(StandardCharsets.UTF_8));
	}
	
}
