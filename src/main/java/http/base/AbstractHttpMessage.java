package http.base;

import java.nio.charset.StandardCharsets;

import http.HttpMessageParseException;

public abstract class AbstractHttpMessage {

	protected static final String CRLF = "\r\n";
	protected static final byte[] CRLFBYTES = CRLF.getBytes(StandardCharsets.US_ASCII);
	
	private final HttpHeaderGroup headerGroup;
	private final HttpMessageBody body;

	public AbstractHttpMessage(HttpHeaderGroup headerGroup, HttpMessageBody body) {
		this.headerGroup = headerGroup;
		this.body = body;
	}

	public HttpHeaderGroup headerGroup() {
		return headerGroup;
	}
	
	public HttpMessageBody body() {
		return body;
	}
	
	abstract public byte[] getBytes() throws HttpMessageParseException;
	abstract public boolean isKeepAlive();
}
