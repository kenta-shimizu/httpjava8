package http.util;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import http.base.AbstractHttpServer;
import http.base.HttpMessageParseException;
import http.base.HttpMessageWriter;
import http.base.HttpReadMessageException;
import http.base.HttpRequestMessage;
import http.base.HttpStatus;
import http.base.HttpVersion;
import http.base.HttpWriteMessageException;

public class HttpServer extends AbstractHttpServer {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	protected ExecutorService executorService() {
		return execServ;
	}
	
	private final HttpServerConfig config;
	
	public HttpServer(HttpServerConfig config) {
		super(config);
		
		this.config = config;
	}
	
	@Override
	public void open() throws IOException {
		
		execServ.execute(() -> {
			
			try {
				for ( ;; ) {
					
					try {
						openServer();
					}
					catch ( InterruptedException e ) {
						throw e;
					}
					catch ( Throwable t ) {
						
						putLog(t);
						
						if ( t instanceof RuntimeException ) {
							throw (RuntimeException)t;
						}
						
						if ( t instanceof Error ) {
							throw (Error)t;
						}
					}
					
					long t = (long)(config.reBindSeconds() * 1000.0F);
					TimeUnit.MILLISECONDS.sleep(t);
				}
			}
			catch (InterruptedException ignore) {
			}
		});
	}
	
	private void openServer() throws InterruptedException, IOException {
		
		try (
				AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
				) {
			
			server.bind(config.serverAddress());
			
			server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
				
				@Override
				public void completed(AsynchronousSocketChannel channel, Void attachment) {
					
					server.accept(null, this);
					
					execServ.execute(() -> {
						
						try {
							reading(channel);
						}
						catch ( InterruptedException ignore ) {
						}
						catch ( Throwable t ) {
							
							putLog(t);
							
							if ( t instanceof RuntimeException ) {
								throw (RuntimeException)t;
							}
							
							if ( t instanceof Error ) {
								throw (Error)t;
							}
						}
						finally {
							
							try {
								channel.shutdownOutput();
							}
							catch ( IOException giveup ) {
							}
							
							try {
								channel.close();
							}
							catch ( IOException e ) {
								putLog(e);
							}
						}
					});
				}
				
				@Override
				public void failed(Throwable t, Void attachment) {
					
					putLog(t);
					
					synchronized ( server ) {
						server.notifyAll();
					}
				}
			});
			
			synchronized ( server ) {
				server.wait();
			}
		}
	}
	
	protected void reading(AsynchronousSocketChannel channel)
			throws InterruptedException
			, ExecutionException
			, HttpReadMessageException
			, HttpWriteMessageException
			, HttpMessageParseException {
		
		final ByteBuffer buffer = ByteBuffer.allocate(1024);
		final HttpMessageWriter writer = HttpMessageWriter.get(channel);
		
		HttpRequestMessageReader reader = new HttpRequestMessageReader();
		
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
					
					Optional<HttpRequestMessage> op = reader.put(buffer);
					
					if ( op.isPresent() ) {
						writeResponseMessage(writer, op.get());
						reader = new HttpRequestMessageReader();
					}
				}
			}
			catch ( InterruptedException e ) {
				f.cancel(true);
				throw e;
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		
		IOException ioExcept = null;
		
		try {
			execServ.shutdown();
			if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
				execServ.shutdownNow();
				if ( ! execServ.awaitTermination(5L, TimeUnit.SECONDS) ) {
					ioExcept = new IOException("ExecutorService#shutdown failed");
				}
			}
			
		}
		catch ( InterruptedException ignore ) {
		}
		
		if ( ioExcept != null ) {
			throw ioExcept;
		}
	}
	
	protected void writeResponseMessage(HttpMessageWriter writer, HttpRequestMessage request)
			throws InterruptedException, HttpWriteMessageException, HttpMessageParseException {
		
		writer.write(HttpResponseMessageBuilders.get(HttpVersion.HTTP1_1).create(HttpStatus.INTERNAL_SERVER_ERROR));
	}

}
