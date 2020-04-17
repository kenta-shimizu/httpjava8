package com.shimizukenta.httpserver;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsynchronousSocketChannelHttpMessageWriter implements HttpMessageWriter {
	
	private final AsynchronousSocketChannel channel;
	
	public AsynchronousSocketChannelHttpMessageWriter(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}
	
	@Override
	public void write(byte[] bs) throws InterruptedException, HttpWriteException {
		
		ByteBuffer buffer = ByteBuffer.allocate(bs.length);
		buffer.put(bs);
		((Buffer)buffer).flip();
		
		while ( buffer.hasRemaining() ) {
			
			final Future<Integer> f = channel.write(buffer);
			
			try {
				int w = f.get().intValue();
				
				if ( w <= 0 ) {
					return;
				}
			}
			catch ( InterruptedException e ) {
				f.cancel(true);
				throw e;
			}
			catch ( ExecutionException e ) {
				
				Throwable t = e.getCause();
				
				if ( t instanceof ClosedChannelException ) {
					throw new HttpClosedChannelWriteException(t);
				}
				
				if ( t instanceof RuntimeException ) {
					throw (RuntimeException)t;
				}
				
				if ( t instanceof Error ) {
					throw (Error)t;
				}
				
				throw new HttpWriteException(e);
			}
		}
	}

}
