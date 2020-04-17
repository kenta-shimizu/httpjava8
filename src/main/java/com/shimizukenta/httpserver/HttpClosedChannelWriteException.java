package com.shimizukenta.httpserver;

public class HttpClosedChannelWriteException extends HttpWriteException {
	
	private static final long serialVersionUID = 7662357927768503589L;
	
	public HttpClosedChannelWriteException() {
		super();
	}

	public HttpClosedChannelWriteException(String message) {
		super(message);
	}

	public HttpClosedChannelWriteException(Throwable cause) {
		super(cause);
	}

	public HttpClosedChannelWriteException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpClosedChannelWriteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
