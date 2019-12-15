package http.base;

import http.HttpBaseException;

public class HttpReadMessageException extends HttpBaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9125767983686555844L;

	public HttpReadMessageException() {
		super();
	}

	public HttpReadMessageException(String message) {
		super(message);
	}

	public HttpReadMessageException(Throwable cause) {
		super(cause);
	}

	public HttpReadMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpReadMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
