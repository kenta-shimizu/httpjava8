package httpUtil;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class HttpClientConnectionPool implements Closeable {
	
	private final Map<SocketAddress, AsynchronousSocketChannel[]> map = new HashMap<>();
	
	private final AtomicInteger autoNumber = new AtomicInteger(0);
	
	private final int maxConnection;
	
	private boolean closed;
	
	public HttpClientConnectionPool(int maxConnect) {
		this.maxConnection = maxConnect;
		this.closed = false;
	}
	
	public AsynchronousSocketChannel get(SocketAddress addr)
			throws IOException, InterruptedException {
		
		synchronized ( this ) {
			
			if ( closed ) {
				throw new IOException("Already closed");
			}
			
			final int autoNum = getIndex();
			
			AsynchronousSocketChannel[] channels = map.computeIfAbsent(addr, x -> {
				
				AsynchronousSocketChannel[] vv = new AsynchronousSocketChannel[maxConnection];
				
				for ( int i = 0, m = vv.length; i < m; ++i ) {
					vv[i] = null;
				}
				
				return vv;
			});
			
			AsynchronousSocketChannel channel = channels[autoNum];
			
			if ( channel != null ) {
				
				if ( channel.isOpen() ) {
					
					return channel;
					
				} else {
					
					closeChannel(channel);
				}
			}
			
			channel = openChannel(addr);
			channels[autoNum] = channel;
			return channel;
		}
	}
	
	private int getIndex() {
		int i = autoNumber.getAndIncrement() % maxConnection;
		return i < 0 ? -i : i;
	}
	
	@Override
	public void close() throws IOException {
		
		synchronized ( this ) {
			
			if ( closed ) return;
			
			closed = true;
			
			IOException ioExcept = null;
			
			for ( AsynchronousSocketChannel[] channels : map.values() ) {
				
				for ( AsynchronousSocketChannel channel : channels ) {
					
					if ( channel != null ) {
						
						try {
							staticCloseChannel(channel);
						}
						catch ( IOException e ) {
							ioExcept = e;
						}
					}
				}
			}
			
			map.clear();
			
			if ( ioExcept != null ) {
				throw ioExcept;
			}
		}
	}
	
	private static AsynchronousSocketChannel openChannel(SocketAddress addr)
			throws IOException, InterruptedException {
		
		AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
		
		Future<Void> f = channel.connect(addr);
		
		try {
			f.get();
			return channel;
		}
		catch ( InterruptedException e ) {
			
			f.cancel(true);
			
			try {
				channel.close();
			}
			catch ( IOException giveup ) {
			}
			
			throw e;
		}
		catch ( ExecutionException e ) {
			
			try {
				channel.close();
			}
			catch ( IOException giveup ) {
			}
			
			Throwable t = e.getCause();
			
			if ( t instanceof IOException ) {
				throw (IOException)t;
			}
			
			if ( t instanceof RuntimeException ) {
				throw (RuntimeException)t;
			}
			
			if ( t instanceof Error ) {
				throw (Error)t;
			}
			
			throw new IOException(e);
		}
	}
	
	public void closeChannel(AsynchronousSocketChannel channel) throws IOException {
		
		synchronized ( this ) {
			
			for ( SocketAddress addr : map.keySet() ) {
				
				AsynchronousSocketChannel[] channels = map.get(addr);
				
				for ( int i = 0, m = channels.length; i < m ; ++i ) {
					
					if ( Objects.equals(channels[i], channel) ) {
						
						channels[i] = null;
						
						if ( Stream.of(channels).allMatch(Objects::isNull) ) {
							map.remove(addr);
						}
						
						staticCloseChannel(channel);
						
						return ;
					}
				}
			}
		}
	}
	
	private static void staticCloseChannel(AsynchronousSocketChannel channel) throws IOException {
		
		try {
			channel.shutdownOutput();
		}
		catch (ClosedChannelException ignore ) {
		}
		
		channel.close();
	}
	
}
