package com.shimizukenta.http;

public class HttpReadException extends HttpBaseException {
	
	private static final long serialVersionUID = 8020095701562798844L;

	public HttpReadException() {
		super();
	}

	public HttpReadException(String message) {
		super(message);
	}

	public HttpReadException(Throwable cause) {
		super(cause);
	}

	public HttpReadException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
