package com.shimizukenta.http;

public class HttpMessageParseException extends HttpBaseException {

	private static final long serialVersionUID = -4760328009644230959L;

	public HttpMessageParseException() {
		super();
	}

	public HttpMessageParseException(String message) {
		super(message);
	}

	public HttpMessageParseException(Throwable cause) {
		super(cause);
	}

	public HttpMessageParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpMessageParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
