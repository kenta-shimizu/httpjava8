package httpBase;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class HttpMessageBytesBodyReader extends HttpMessageBodyReader {
	
	private final ByteArrayOutputStream os;
	private final int length;
	
	public HttpMessageBytesBodyReader(int length) {
		super();
		
		this.os = new ByteArrayOutputStream(length);
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
			return os.size() == length;
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
