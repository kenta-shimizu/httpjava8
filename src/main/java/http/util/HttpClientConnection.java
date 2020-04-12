package http.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.shimizukenta.http.HttpLog;
import com.shimizukenta.http.HttpLogListener;

import http.HttpMessageParseException;
import http.HttpWriteMessageClosedChannelException;
import http.HttpWriteMessageException;
import http.base.HttpMessageWriter;
import http.base.HttpReadMessageException;
import http.base.HttpRequestMessagePack;
import http.base.HttpResponseMessage;
import http.base.HttpResponseMessagePack;
import http.base.HttpResponseMessagePackListener;

public class HttpClientConnection implements Closeable {
	
	private final BlockingQueue<HttpRequestMessagePack> requestQueue = new LinkedBlockingQueue<>();
	
	private final SocketAddress addr;
	private final HttpResponseMessagePackListener reponseMsgListener;
	private final Executor executor;
	
	private AsynchronousSocketChannel channel;
	private HttpMessageWriter writer;
	
	private boolean opened;
	private boolean closed;
	
	public HttpClientConnection(SocketAddress addr, HttpResponseMessagePackListener lstnr, Executor executor) {
		this.addr = addr;
		this.reponseMsgListener = lstnr;
		this.executor = executor;
		this.channel = null;
		this.writer = null;
		this.opened = false;
		this.closed = false;
	}
	
	public static HttpClientConnection get(SocketAddress addr, HttpResponseMessagePackListener lstnr, Executor execServ)
			throws IOException, InterruptedException {
		
		HttpClientConnection inst = new HttpClientConnection(addr, lstnr, execServ);
		
		try {
			inst.open();
		}
		catch ( IOException e ) {
			
			try {
				inst.close();
			}
			catch ( IOException giveup ) {
			}
			
			throw e;
		}
		
		return inst;
	}
	
	public boolean isOpen() {
		synchronized ( this ) {
			return opened && ! closed;
		}
	}
	
	public void open() throws IOException, InterruptedException {
		
		synchronized ( this ) {
			
			if ( closed ) {
				throw new IOException("Already closed");
			}
			
			if ( opened ) {
				throw new IOException("Already opened");
			}
			
			channel = openChannel(addr);
			writer = HttpMessageWriter.get(channel);
			
			opened = true;
			
			executor.execute(() -> {
				
				try {
					reading();
				}
				catch ( InterruptedException ignore ) {
				}
				catch ( HttpReadMessageException | IOException e ) {
					putLog(e);
				}
				
				try {
					close();
				}
				catch ( IOException e ) {
					putLog(e);
				}
			});
		}
	}
	
	private void reading() throws InterruptedException, HttpReadMessageException, IOException {
		
		final ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		HttpResponseMessageReader reader = new HttpResponseMessageReader();
		
		for ( ;; ) {
			
			((Buffer)buffer).clear();
			
			Future<Integer> f = channel.read(buffer);
			
			try {
				int r = f.get().intValue();
				
				if ( r < 0 ) {
					return;
				}
				
				((Buffer)buffer).flip();
				
				while ( buffer.hasRemaining() ) {
					
					Optional<HttpResponseMessage> op = reader.put(buffer);
					
					if ( op.isPresent() ) {
						
						HttpResponseMessage rsp = op.get();
						
						boolean keepAlive = rsp.keepAlive();
						
						if ( ! keepAlive ) {
							close();
						}
						
						HttpRequestMessagePack req = requestQueue.poll();
						
						if ( req != null ) {
							reponseMsgListener.receive(new HttpResponseMessagePack(req, rsp));
						}
						
						if ( ! keepAlive ) {
							return;
						}
						
						reader = new HttpResponseMessageReader();
					}
				}
			}
			catch ( InterruptedException e ) {
				f.cancel(true);
				throw e;
			}
			catch ( ExecutionException e ) {
				
				Throwable t = e.getCause();
				
				if ( t instanceof RuntimeException ) {
					throw (RuntimeException)t;
				}
				
				if ( t instanceof Error ) {
					throw (Error)t;
				}
				
				throw new HttpReadMessageException(e);
			}
		}
	}
	
	public void request(HttpRequestMessagePack request)
			throws InterruptedException
			, HttpWriteMessageClosedChannelException
			, HttpWriteMessageException
			, HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( ! isOpen() ) {
				throw new HttpWriteMessageException("Connection not opened");
			}
			
			if ( writer == null ) {
				throw new HttpWriteMessageException("writer not setted");
			}
			
			requestQueue.put(request);
			writer.write(request);
		}
	}
	
	@Override
	public void close() throws IOException {
		
		synchronized ( this ) {
			
			if ( closed ) {
				return;
			}
			
			closed = true;
			
			if ( channel != null ) {
				
				try {
					channel.shutdownOutput();
				}
				catch ( ClosedChannelException ignore ) {
				}
				
				channel.close();
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
	
	
	/*** Logging ***/
	private final Collection<HttpLogListener> logListeners = new CopyOnWriteArrayList<>();
	
	public boolean addLogListener(HttpLogListener lstnr) {
		return logListeners.add(lstnr);
	}
	
	public boolean removeLogListener(HttpLogListener lstnr) {
		return logListeners.remove(lstnr);
	}
	
	protected void putLog(HttpLog log) {
		logListeners.forEach(lstnr -> {
			lstnr.receive(log);
		});
	}
	
	protected void putLog(Throwable t) {
		putLog(new HttpLog(t));
	}
	
}
