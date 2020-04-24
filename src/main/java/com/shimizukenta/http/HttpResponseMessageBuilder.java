package com.shimizukenta.http;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public interface HttpResponseMessageBuilder {
	
	default public HttpResponseMessage build(HttpStatus status) {
		return build(status, HttpMessageHeaderGroup.empty(), HttpMessageBody.empty());
	}
	
	default public HttpResponseMessage build(HttpStatus status, HttpMessageHeaderGroup headerGroup) {
		return build(status, headerGroup, HttpMessageBody.empty());
	}
	
	public HttpResponseMessage build(HttpStatus status, HttpMessageHeaderGroup headerGroup, AbstractHttpMessageBody body);
	
	/**
	 * build Response Message from File
	 * 
	 * @param request
	 * @param connectionValue
	 * @param path
	 * @return Response-Message
	 */
	default public HttpResponseMessage fromFile(
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue,
			Path path) {
		
		return fromFile(
				request,
				connectionValue,
				path,
				HttpContentType.get(path));
	}
	
	/**
	 * build Response Message from File
	 * 
	 * @param request
	 * @param connectionValue
	 * @param path
	 * @param Content-Type
	 * @return Response-Message
	 */
	public HttpResponseMessage fromFile(
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue,
			Path path,
			HttpContentType type);
	
	/**
	 * build Response Message of application/json
	 * 
	 * @param request
	 * @param connectionValue
	 * @param json
	 * @return Response-Message
	 */
	public HttpResponseMessage fromJson(
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue,
			byte[] json);
	
	/**
	 * build Response Message of application/json
	 * parse UTF-8<br />
	 * 
	 * @param request
	 * @param connectionValue
	 * @param json
	 * @return Response-Message
	 */
	default public HttpResponseMessage fromJson(
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue,
			CharSequence json) {
		
		return fromJson(
				request,
				connectionValue,
				json.toString().getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * build Response Message of text/xml<br />
	 * 
	 * @param request
	 * @param connectionValue
	 * @param xml
	 * @return Response-Message
	 */
	public HttpResponseMessage fromXml(
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue,
			byte[] xml);
	
	/**
	 * build Response Message of text/xml<br />
	 * parse UTF-8<br />
	 * 
	 * @param request
	 * @param connectionValue
	 * @param xml
	 * @return Response-Message
	 */
	default public HttpResponseMessage fromXml(
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue,
			CharSequence xml) {
		
		return fromXml(
				request,
				connectionValue,
				xml.toString().getBytes(StandardCharsets.UTF_8));
	}
	
}
