package http.base;

import java.nio.ByteBuffer;

public abstract class AbstractHttpMessageBodyReader {
	
	public AbstractHttpMessageBodyReader() {
		/* Nothing */
	}
	
	/**
	 * 
	 * @param buffer
	 * @return true if completed
	 */
	abstract public boolean put(ByteBuffer buffer) throws HttpMessageParseException;
	
	abstract public boolean completed();
	abstract public HttpMessageBody getHttpMessageBody();
	abstract public HttpHeaderGroup trailer();
}
