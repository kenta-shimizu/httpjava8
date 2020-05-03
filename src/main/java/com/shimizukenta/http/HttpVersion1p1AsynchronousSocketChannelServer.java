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
	
	private static final long reconnectSeconds = 5L;
	
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
						
						putLog(new HttpLog("Server-bind", addr));
						
						server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

							@Override
							public void completed(AsynchronousSocketChannel channel, Void attachment) {
								
								server.accept(null, this);
								
								final String channelStr = channel.toString();
								
								putLog(new HttpLog("Channel-accepted", channelStr));
								
								final HttpServerConnectionValue connectionValue = createServerConnectionValue(channel);
								final HttpMessageWriter writer = new AsynchronousSocketChannelHttpMessageWriter(channel);
								final HttpRequestMessageReader reader = new AsynchronousSocketChannelHttpRequestMessageReader(channel, config.keepAliveTimeout());
								
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
												
												final SocketAddress localAddr = channel.getLocalAddress();
												final SocketAddress remoteAddr = channel.getRemoteAddress();

												for ( ;; ) {
													
													final HttpRequestMessage request = reader.read();
													
													if ( request == null ) {
														break;
													}
													
													final HttpRequestMessageInformation reqInfo = HttpRequestMessageInformation.from(request, localAddr, remoteAddr);
													
													putRequestMessageLog(reqInfo.log());
													
													boolean keepAlive = tryService(writer, reqInfo, connectionValue);
													
													if ( ! keepAlive ) {
														break;
													}
												}
											}
											catch ( InterruptedException ignore ) {
											}
											catch ( HttpReadException | HttpWriteException | IOException e) {
												putLog(e);
											}
											
											return null;
										}
								);
								
								try {
									execServ.invokeAny(tasks);
								}
								catch (ExecutionException e ) {
									putLog(e.getCause());
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
									catch ( IOException e ) {
										putLog(e);
									}
								}
								
								putLog(new HttpLog("Channel-closed", channelStr));
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
					catch ( IOException e ) {
						putLog(e);
					}
					
					TimeUnit.SECONDS.sleep(reconnectSeconds);
				}
			}
			catch ( InterruptedException ignore ) {
			}
		});
	}
	
	private boolean tryService(
			HttpMessageWriter writer,
			HttpRequestMessageInformation requestInfo,
			HttpServerConnectionValue connectionValue)
					throws InterruptedException, HttpWriteException {
		
		for ( HttpServerService s : this.services() ) {
			if ( s.accept(requestInfo) ) {
				return s.tryService(writer, requestInfo, connectionValue);
			}
		}
		
		return responseInternalServerError(writer, requestInfo);
	}
	
	private boolean responseInternalServerError(
			HttpMessageWriter writer,
			HttpRequestMessageInformation requestInfo)
					throws InterruptedException, HttpWriteException {
		
		HttpResponseMessage rspMsg = msgBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR);
		
		HttpResponseMessageLog rspLog =	HttpResponseMessageLog.from(
				rspMsg,
				requestInfo.getLocalAddress(),
				requestInfo.getRemoteAddress());
		
		putResponseMessageLog(rspLog);
		putAccessLog(new HttpAccessLog(requestInfo.log(), rspLog));
		
		writer.write(rspMsg);
		
		return false;
	}
	
	@Override
	public boolean addServerService(HttpServerService s) {
		s.addLogListener(this::putLog);
		s.addResponseMessageLogListener(this::putResponseMessageLog);
		s.addAccessLogListener(this::putAccessLog);
		return super.addServerService(s);
	}
	
	@Override
	public boolean removeServerService(HttpServerService s) {
		s.removeLogListener(this::putLog);
		s.removeResponseMessageLogListener(this::putResponseMessageLog);
		s.removeAccessLogListener(this::putAccessLog);
		return super.removeServerService(s);
	}
	
	private void putLog(Throwable t) {
		putLog(new HttpLog(t));
	}

}
