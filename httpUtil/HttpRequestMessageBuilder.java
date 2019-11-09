package httpUtil;

import java.net.SocketAddress;

import httpBase.HttpHeaderGroup;
import httpBase.HttpMessageBody;
import httpBase.HttpRequestLine;
import httpBase.HttpRequestMessagePack;
import httpBase.HttpVersion;

public abstract class HttpRequestMessageBuilder {

	protected HttpRequestMessageBuilder() {
		/* Nothing */
	}
	
	public HttpRequestMessagePack create(
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
	
	abstract public HttpRequestMessagePack createGet(CharSequence absoluteUri) throws AbsoluteUriParseException;
	abstract public HttpRequestMessagePack createHead(CharSequence absoluteUri) throws AbsoluteUriParseException;
	abstract public HttpRequestMessagePack createPost(CharSequence absoluteUri, HttpMessageBody body) throws AbsoluteUriParseException;
	
}
