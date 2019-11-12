package http.base;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
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
	
	protected HttpMessageWriter(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}
	
	public static HttpMessageWriter get(AsynchronousSocketChannel channel) {
		return new HttpMessageWriter(channel);
	}
	
	public void write(AbstractHttpMessage msg) throws InterruptedException, HttpWriteMessageClosedChannelException, HttpWriteMessageException, HttpMessageParseException {
		write(msg.getBytes());
	}
	
	public void write(byte[] bs) throws InterruptedException, HttpWriteMessageClosedChannelException, HttpWriteMessageException {
		
		ByteBuffer buffer = ByteBuffer.allocate(bs.length);
		buffer.put(bs);
		((Buffer)buffer).flip();
		
		write(buffer);
	}
	
	public void writeWithCrLf() throws InterruptedException, HttpWriteMessageClosedChannelException, HttpWriteMessageException {
		writeWithCrLf(Collections.singletonList(new byte[0]));
	}
	
	public void writeWithCrLf(List<byte[]> bss) throws InterruptedException, HttpWriteMessageClosedChannelException, HttpWriteMessageException {
		
		int size = bss.stream().mapToInt(bs -> bs.length + CRLFBYTESLENGTH).sum();
		
		ByteBuffer buffer = ByteBuffer.allocate(size);
		
		bss.stream().forEach(bs -> {
			buffer.put(bs).put(CRLFBYTES);
		});
		
		((Buffer)buffer).flip();
		
		write(buffer);
	}
	
	private void write(ByteBuffer buffer) throws InterruptedException, HttpWriteMessageClosedChannelException, HttpWriteMessageException {
		
		while ( buffer.hasRemaining() ) {
			
			Future<Integer> f = channel.write(buffer);
			
			try {
				int w = f.get().intValue();
				
				if ( w <= 0 ) {
					return;
				}
			}
			catch ( ExecutionException e ) {
				
				Throwable t = e.getCause();
				
				if ( t instanceof ClosedChannelException ) {
					throw new HttpWriteMessageClosedChannelException(t);
				}
				
				if ( t instanceof RuntimeException ) {
					throw (RuntimeException)t;
				}
				
				if ( t instanceof Error ) {
					throw (Error)t;
				}
				
				throw new HttpWriteMessageException(e);
			}
			catch ( InterruptedException e ) {
				f.cancel(true);
				throw e;
			}
		}
	}
}
