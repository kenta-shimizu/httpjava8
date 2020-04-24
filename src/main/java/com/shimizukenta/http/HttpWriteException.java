package com.shimizukenta.http;

public class HttpWriteException extends HttpBaseException {
	
	private static final long serialVersionUID = 5064988446160872234L;
	
	public HttpWriteException() {
		super();
	}

	public HttpWriteException(String message) {
		super(message);
	}

	public HttpWriteException(Throwable cause) {
		super(cause);
	}

	public HttpWriteException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpWriteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
