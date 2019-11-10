package httpUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import httpBase.AbstractHttpMessageBodyReader;
import httpBase.CrLfLineReader;
import httpBase.HttpHeaderField;
import httpBase.HttpHeaderGroup;
import httpBase.HttpMessageBytesBodyReader;
import httpBase.HttpMessageChunkBodyReader;
import httpBase.HttpMessageParseException;
import httpBase.HttpReadMessageException;
import httpBase.HttpRequestLine;
import httpBase.HttpRequestMessage;

public class HttpRequestMessageReader {
	
	private final CrLfLineReader crlfr = new CrLfLineReader(1024);
	
	private HttpRequestLine requestLine;
	private final List<String> headers = new ArrayList<>();
	private HttpHeaderGroup headerGroup;
	private AbstractHttpMessageBodyReader bodyReader;
	
	public HttpRequestMessageReader() {
		requestLine = null;
		headerGroup = null;
		bodyReader = null;
	}
	
	public Optional<HttpRequestMessage> put(ByteBuffer buffer) throws HttpReadMessageException {
		
		try {
			
			while ( buffer.hasRemaining() ) {
				
				if ( requestLine == null ) {
					
					crlfr.put(buffer).ifPresent(bs -> {
						requestLine = new HttpRequestLine(bs);
					});
					
					continue;
				}
				
				if ( headerGroup == null ) {
					
					Optional<byte[]> op = crlfr.put(buffer);
					
					if ( op.isPresent() ) {
						
						byte[] bs = op.get();
						
						if ( bs.length > 0 ) {
							
							headers.add(new String(bs, StandardCharsets.US_ASCII));
							continue;
							
						} else {
							
							headerGroup = HttpHeaderGroup.lines(headers);
							bodyReader = createBodyReader();
						}
					}
				}
				
				if ( bodyReader != null ) {
					
					if ( bodyReader.put(buffer) ) {
						
						return Optional.of(
								new HttpRequestMessage(
										requestLine
										, headerGroup
										, bodyReader.getHttpMessageBody()));
					}
				}
			}
			
			return Optional.empty();
		}
		catch ( HttpMessageParseException e ) {
			throw new HttpReadMessageException(e);
		}
	}
	
	private AbstractHttpMessageBodyReader createBodyReader() throws HttpMessageParseException {
		
		{
			Optional<String> op = headerGroup.getFieldValue(HttpHeaderField.TransferEncoding);
			
			if ( op.filter(v -> v.equals("chunked")).isPresent() ) {
				
				return new HttpMessageChunkBodyReader();
			}
		}
		
		{
			Optional<String> op = headerGroup.getFieldValue(HttpHeaderField.ContentLength);
			
			if ( op.isPresent() ) {
				
				try {
					int len = Integer.parseInt(op.get());
					
					if ( len < 0 ) {
						throw new HttpMessageParseException("Content-Length < 0");
					}
					
					return new HttpMessageBytesBodyReader(len);
				}
				catch (NumberFormatException e) {
					throw new HttpMessageParseException(e);
				}
				
			} else {
				
				return new HttpMessageBytesBodyReader(0);
			}
		}
	}

}
