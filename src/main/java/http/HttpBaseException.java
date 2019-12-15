package http;

public class HttpBaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6310184751643065853L;

	public HttpBaseException() {
		super();
	}

	public HttpBaseException(String message) {
		super(message);
	}

	public HttpBaseException(Throwable cause) {
		super(cause);
	}

	public HttpBaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
