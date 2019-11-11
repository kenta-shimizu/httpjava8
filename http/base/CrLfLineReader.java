package http.base;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

public class CrLfLineReader {
	
	private static final byte CR = (byte)0xD;
	private static final byte LF = (byte)0xA;

	private ByteArrayOutputStream os;
	private boolean detectCr;
	
	public CrLfLineReader() {
		this.os = new ByteArrayOutputStream();
		this.detectCr = false;
	}
	
	public CrLfLineReader(int length) {
		this.os = new ByteArrayOutputStream(length);
		this.detectCr = false;
	}
	
	public Optional<byte[]> put(ByteBuffer buffer) {
		
		synchronized ( this ) {
			
			while ( buffer.hasRemaining() ) {
				
				byte b = buffer.get();
				
				if ( b == CR ) {
					
					if ( detectCr ) {
						
						os.write(CR);
						
					} else {
						
						detectCr = true;
					}
					
				} else if ( b == LF ) {
					
					byte[] bs = os.toByteArray();
					
					os.reset();
					detectCr = false;
					
					return Optional.of(bs);
					
				} else {
					
					if ( detectCr ) {
						os.write(CR);
						detectCr = false;
					}
					
					os.write(b);
				}
			}
		}
		
		return Optional.empty();
	}
	
}
