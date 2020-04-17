package http.base;

import java.nio.ByteBuffer;

import com.shimizukenta.httpserver.HttpMessageHeaderGroup;
import com.shimizukenta.httpserver.HttpMessageParseException;

public interface HttpMessageBodyReadable {
	
	/**
	 * 
	 * @param buffer
	 * @return true if completed
	 */
	public boolean put(ByteBuffer buffer) throws HttpMessageParseException;
	
	public boolean completed();
	public HttpMessageBody getHttpMessageBody();
	public HttpMessageHeaderGroup trailer();
}
