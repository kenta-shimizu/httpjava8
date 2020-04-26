package com.shimizukenta.http;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpVersion1p1AsynchronousSocketChannelServer extends AbstractHttpServer {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final HttpResponseMessageBuilder msgBuilder;

	private final HttpVersion1p1AsynchronousSocketChannelServerConfig config;
	private boolean opened;
	private boolean closed;
	
	public HttpVersion1p1AsynchronousSocketChannelServer(HttpVersion1p1AsynchronousSocketChannelServerConfig config) {
		super();
		this.config = config;
		this.opened = false;
		this.closed = false;
		
		final HttpVersion1p1ResponseMessageBuilderConfig msgBuilderConfig = new HttpVersion1p1ResponseMessageBuilderConfig();
		this.msgBuilder = new HttpVersion1p1ResponseMessageBuilder(msgBuilderConfig);
	}

	@Override
	public void open() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.closed ) {
				throw new IOException("Already closed");
			}
			
			if ( this.opened ) {
				throw new IOException("Already opened");
			}
			
			this.opened = true;
		}
		
		config.binds().forEach(this::openBind);
	}

	@Override
	public void close() throws IOException {
		
		synchronized ( this ) {
			if ( this.closed ) {
				return;
			}
			this.closed = true;
		}
		
		try {
			execServ.shutdown();
			if ( ! execServ.awaitTermination(1, TimeUnit.MILLISECONDS) ) {
				execServ.shutdownNow();
				if ( ! execServ.awaitTermination(5L, TimeUnit.SECONDS) ) {
					putLog(new HttpLog("ExecutorService#shutdown failed"));
				}
			}
		}
		catch ( InterruptedException giveup ) {
		}
	}
	
	private HttpServerConnectionValue createServerConnectionValue(AsynchronousSocketChannel channel) {
		return new HttpServerConnectionValue(
				channel,
				new HttpKeepAliveValue(
						config.keepAliveTimeout(),
						config.keepAliveMax()));
	}
	
	private void openBind(SocketAddress addr) {
		
		execServ.execute(() -> {
			
			try {
				for ( ;; ) {
					
					try (
							AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
							) {
						
						server.bind(addr);
						
						server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

							@Override
							public void completed(AsynchronousSocketChannel channel, Void attachment) {
								
								server.accept(null, this);
								
								final HttpServerConnectionValue connectionValue = createServerConnectionValue(channel);
								final HttpMessageWriter writer = new AsynchronousSocketChannelHttpMessageWriter(channel);
								
								final Collection<Callable<Object>> tasks = Arrays.asList(
										() -> {
											try {
												synchronized ( server ) {
													server.wait();
												}
											}
											catch ( InterruptedException ignore ) {
											}
											
											return null;
										},
										() -> {
											
											try {
												for ( ;; ) {
													
													final HttpRequestMessage request = readRequest(channel);
													
													if ( request == null ) {
														break;
													}
													
													boolean keepAlive = tryService(writer, request, connectionValue);
													
													if ( ! keepAlive ) {
														break;
													}
												}
											}
											catch ( InterruptedException ignore ) {
											}
											
											return null;
										}
								);
								
								try {
									execServ.invokeAny(tasks);
								}
								catch (ExecutionException e ) {
									
									//TODO
									
									putLog(new HttpLog(e));
								}
								catch ( InterruptedException ignore ) {
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
									catch ( IOException giveup ) {
									}
								}
							}

							@Override
							public void failed(Throwable t, Void attachment) {
								
								putLog(new HttpLog(t));
								
								synchronized ( server ) {
									server.notifyAll();
								}
							}
						});
						
						synchronized ( server ) {
							server.wait();
						}
					}
					catch ( IOException e ) {
						putLog(new HttpLog(e));
					}
				}
			}
			catch ( InterruptedException ignore ) {
			}
		});
	}
	
	private HttpRequestMessage readRequest(AsynchronousSocketChannel channel) throws InterruptedException {
		
		//TODO
		
		return null;
	}
	
	private boolean tryService(
			HttpMessageWriter writer,
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue)
					throws InterruptedException, HttpWriteException {
		
		for ( HttpServerService s : this.services() ) {
			if ( s.accept(request) ) {
				return s.tryService(writer, request, connectionValue);
			}
		}
		
		writer.write(msgBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR));
		return false;
	}

}
