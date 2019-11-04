package httpUtil;

import httpBase.HttpHeaderGroup;
import httpBase.HttpMessageBody;
import httpBase.HttpRequestLine;
import httpBase.HttpRequestMessage;
import httpBase.HttpVersion;

public abstract class HttpRequestMessageBuilder {

	protected HttpRequestMessageBuilder() {
		/* Nothing */
	}
	
	public HttpRequestMessage create(
			CharSequence method
			, CharSequence uri
			, HttpHeaderGroup headerGroup
			, HttpMessageBody body) {
		
		HttpRequestLine requestLine = new HttpRequestLine(
				method.toString()
				+ " " + uri.toString()
				+ " " + getHttpVersion().toString());
		
		return new HttpRequestMessage(requestLine, headerGroup, body);
	}
	
	abstract public HttpVersion getHttpVersion();
	
	abstract public HttpRequestMessage createGet(CharSequence absoluteUri) throws AbsoluteUriParseException;
	abstract public HttpRequestMessage createHead(CharSequence absoluteUri) throws AbsoluteUriParseException;
	abstract public HttpRequestMessage createPost(CharSequence absoluteUri, HttpMessageBody body) throws AbsoluteUriParseException;
	
}
