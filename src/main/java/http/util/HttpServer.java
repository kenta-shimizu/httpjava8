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
import java.util.concurrent.TimeoutException;

import http.HttpLog;
import http.HttpMessageParseException;
import http.HttpServerServiceSupplier;
import http.HttpStatus;
import http.HttpVersion;
import http.HttpWriteMessageException;
import http.base.AbstractHttpServer;
import http.base.HttpKeepAliveValue;
import http.base.HttpMessageWriter;
import http.base.HttpReadMessageException;
import http.base.HttpRequestMessage;
import http.base.HttpServerConnectionValue;

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
	
	private final HttpServerServiceSupplier generalService;
	
	public HttpServer(HttpServerConfig config) {
		super(config);
		
		this.config = config;
		
		this.generalService = new HttpGeneralFileServerService(config.generalFileServerServiceConfig());
	}
	
	public static HttpServer open(HttpServerConfig config) throws IOException {
		
		HttpServer inst = new HttpServer(config);
		
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
	
	@Override
	public void open() throws IOException {
		
		super.open();
		
		execServ.execute(() -> {
			
			try {
				for ( ;; ) {
					
					try {
						openServer();
					}
					catch ( InterruptedException e ) {
						throw e;
					}
					catch ( RuntimeException | Error t ) {
						putLog(t);
						throw t;
					}
					catch ( Throwable t ) {
						putLog(t);
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
			
			putLog(new HttpLog("binded", server));
			
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
							
							if ( t instanceof RuntimeException ) {
								throw (RuntimeException)t;
							}
							
							if ( t instanceof Error ) {
								throw (Error)t;
							}
							
							putLog(t);
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
		
		final HttpKeepAliveValue keepAliveValue = createKeepAliveValue();
		final HttpServerConnectionValue connectionValue = new HttpServerConnectionValue(channel, keepAliveValue);
		
		final ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		final HttpMessageWriter writer = HttpMessageWriter.get(channel);
		writer.addWroteLogListener(this::putResponseLog);
		
		HttpRequestMessageReader reader = new HttpRequestMessageReader();
		
		final String channelInfo = channel.toString();
		
		for ( ;; ) {
			
			((Buffer)buffer).clear();
			
			Future<Integer> f = channel.read(buffer);
					
			try {
				int r = f.get(keepAliveValue.timeout(), TimeUnit.SECONDS).intValue();
				
				if ( r < 0 ) {
					return;
				}
				
				((Buffer)buffer).flip();
				
				while ( buffer.hasRemaining() ) {
					
					Optional<HttpRequestMessage> op = reader.put(buffer);
					
					op.ifPresent(msg -> {
						putAccessLog(channelInfo, msg);
					});
					
					if ( op.isPresent() ) {
						
						if ( ! writeResponseMessage(writer, op.get(), connectionValue) ) {
							return;
						}
						
						reader = new HttpRequestMessageReader();
					}
				}
			}
			catch ( InterruptedException e ) {
				f.cancel(true);
				throw e;
			}
			catch ( TimeoutException e ) {
				f.cancel(true);
				return;
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		
		IOException ioExcept = null;
		
		synchronized ( this ) {
			
			try {
				if ( isClosed() ) {
					return;
				}
				
				super.close();
			}
			catch ( IOException e ) {
				ioExcept = e;
			}
		}
		
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
	
	/**
	 * 
	 * @param writer
	 * @param request
	 * @param connectionValue
	 * @return true if Keep-Alive
	 * @throws InterruptedException
	 * @throws HttpWriteMessageException
	 * @throws HttpMessageParseException
	 */
	protected boolean writeResponseMessage(
			HttpMessageWriter writer
			, HttpRequestMessage request
			, HttpServerConnectionValue connectionValue)
					throws InterruptedException
					, HttpWriteMessageException
					, HttpMessageParseException {
		
		if ( generalService.accept(request) ) {
			return generalService.tryService(writer, request, connectionValue);
		}
		
		writer.write(HttpResponseMessageBuilders.get(HttpVersion.HTTP1_1).build(HttpStatus.INTERNAL_SERVER_ERROR));
		return false;
	}
	
	protected HttpKeepAliveValue createKeepAliveValue() {
		return new HttpKeepAliveValue(config.keepAliveTimeout(), config.keepAliveMax());
	}
	
	protected void putAccessLog(Object channelInfo, HttpRequestMessage msg) {
		putAccessLog(new HttpLog("Message accept", (
				channelInfo.toString()
				+ System.lineSeparator()
				+ msg.toString())));
	}
}
