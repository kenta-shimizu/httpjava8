package http;

import com.shimizukenta.http.HttpBaseException;

public class HttpWriteMessageException extends HttpBaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5462878745172548053L;

	public HttpWriteMessageException() {
		super();
	}

	public HttpWriteMessageException(String message) {
		super(message);
	}

	public HttpWriteMessageException(Throwable cause) {
		super(cause);
	}

	public HttpWriteMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpWriteMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
