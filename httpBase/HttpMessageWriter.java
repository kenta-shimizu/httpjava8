package httpBase;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HttpMessageWriter {
	
	private static final String CRLF = "\r\n";
	private static final byte[] CRLFBYTES = CRLF.getBytes(StandardCharsets.US_ASCII);
	private static final int CRLFBYTESLENGTH = CRLFBYTES.length;
	
	private final AsynchronousSocketChannel channel;
	
	public HttpMessageWriter(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}
	
	public void write(HttpRequestMessage msg) throws InterruptedException, HttpWriteMessageException, HttpMessageParseException {
		write(msg.getBytes());
	}
	
	public void write(HttpResponseMessage msg) throws InterruptedException, HttpWriteMessageException, HttpMessageParseException {
		write(msg.getBytes());
	}
	
	public void write(byte[] bs) throws InterruptedException, HttpWriteMessageException {
		
		ByteBuffer buffer = ByteBuffer.allocate(bs.length);
		buffer.put(bs);
		((Buffer)buffer).flip();
		
		write(buffer);
	}
	
	public void writeWithCrLf() throws InterruptedException, HttpWriteMessageException {
		writeWithCrLf(Collections.singletonList(new byte[0]));
	}
	
	public void writeWithCrLf(List<byte[]> bss) throws InterruptedException, HttpWriteMessageException {
		
		int size = bss.stream().mapToInt(bs -> bs.length + CRLFBYTESLENGTH).sum();
		
		ByteBuffer buffer = ByteBuffer.allocate(size);
		
		bss.stream().forEach(bs -> {
			buffer.put(bs).put(CRLFBYTES);
		});
		
		((Buffer)buffer).flip();
		
		write(buffer);
	}
	
	private boolean write(ByteBuffer buffer) throws InterruptedException, HttpWriteMessageException {
		
		while ( buffer.hasRemaining() ) {
			
			Future<Integer> f = channel.write(buffer);
			
			try {
				int w = f.get().intValue();
				
				if ( w <= 0 ) {
					return false;
				}
			}
			catch ( ExecutionException e ) {
				throw new HttpWriteMessageException(e);
			}
			catch ( InterruptedException e ) {
				f.cancel(true);
				throw e;
			}
		}
		
		return true;
	}
}
