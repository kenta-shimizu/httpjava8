package http.base;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class HttpMessageBytesBodyReader extends AbstractHttpMessageBodyReader {
	
	private final ByteArrayOutputStream os;
	private final int length;
	
	public HttpMessageBytesBodyReader(int length) {
		super();
		
		this.os = new ByteArrayOutputStream(length > 0 ? length : 1);
		this.length = length;
	}

	@Override
	public boolean put(ByteBuffer buffer) {
		
		synchronized ( this ) {
			
			for ( ;; ) {
				
				boolean f = completed();
				if ( f ) {
					return f;
				}
				
				if ( ! buffer.hasRemaining() ) {
					return f;
				}
				
				os.write(buffer.get());
			}
		}
	}

	@Override
	public boolean completed() {
		synchronized ( this ) {
			return os.size() >= length;
		}
	}

	@Override
	public HttpMessageBody getHttpMessageBody() {
		
		synchronized ( this ) {
			
			if ( ! completed() ) {
				throw new IllegalStateException("BytesBodyReader not completed");
			}

			return new HttpMessageBody(os.toByteArray());
		}
	}

	@Override
	public HttpHeaderGroup trailer() {
		return HttpHeaderGroup.empty();
	}
	
}
