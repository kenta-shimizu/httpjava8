package http.util;

import java.net.SocketAddress;

import http.base.HttpHeaderGroup;
import http.base.HttpMessageBody;
import http.base.HttpRequestLine;
import http.base.HttpRequestMessagePack;
import http.base.HttpVersion;

public abstract class HttpRequestMessageBuilder {

	protected HttpRequestMessageBuilder() {
		/* Nothing */
	}
	
	public HttpRequestMessagePack build(
			SocketAddress hostSocketAddress
			, CharSequence method
			, CharSequence uri
			, HttpHeaderGroup headerGroup
			, HttpMessageBody body) {
		
		HttpRequestLine requestLine = new HttpRequestLine(
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
