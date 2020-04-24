package http.base;

import java.nio.ByteBuffer;

import com.shimizukenta.http.HttpMessageHeaderGroup;
import com.shimizukenta.http.HttpMessageParseException;

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
