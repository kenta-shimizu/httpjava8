package http.util;

import java.net.SocketAddress;

import com.shimizukenta.httpserver.HttpMessageHeaderGroup;
import com.shimizukenta.httpserver.HttpMessageRequestLine;
import com.shimizukenta.httpserver.HttpVersion;

import http.base.HttpMessageBody;
import http.base.HttpRequestMessagePack;

public abstract class HttpRequestMessageBuilder {

	protected HttpRequestMessageBuilder() {
		/* Nothing */
	}
	
	public HttpRequestMessagePack build(
			SocketAddress hostSocketAddress
			, CharSequence method
			, CharSequence uri
			, HttpMessageHeaderGroup headerGroup
			, HttpMessageBody body) {
		
		HttpMessageRequestLine requestLine = new HttpMessageRequestLine(
				method.toString()
				+ " " + uri.toString()
				+ " " + getHttpVersion().toString());
		
		return new HttpRequestMessagePack(hostSocketAddress, requestLine, headerGroup, body);
	}
	
	abstract public HttpVersion getHttpVersion();
	
	abstract public HttpRequestMessagePack buildGet(CharSequence absoluteUri) throws AbsoluteUriParseException;
	abstract public HttpRequestMessagePack buildHead(CharSequence absoluteUri) throws AbsoluteUriParseException;
	abstract public HttpRequestMessagePack buildPost(CharSequence absoluteUri, HttpMessageBody body) throws AbsoluteUriParseException;
	
}
