package httpUtil;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import httpBase.AbstractHttpClient;
import httpBase.CrLfLineReader;
import httpBase.HttpHeaderField;
import httpBase.HttpHeaderGroup;
import httpBase.HttpMessageBody;
import httpBase.HttpMessageBytesBodyReader;
import httpBase.HttpMessageChunkBodyReader;
import httpBase.HttpMessageParseException;
import httpBase.HttpMessageWriter;
import httpBase.HttpMethod;
import httpBase.HttpReadMessageException;
import httpBase.HttpRequestMessagePack;
import httpBase.HttpResponseMessagePack;
import httpBase.HttpStatusLine;
import httpBase.HttpWriteMessageClosedChannelException;
import httpBase.HttpWriteMessageException;

public class HttpClient extends AbstractHttpClient {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final HttpClientConnectionPool connectionPool;
	
	private boolean opened;
	private boolean closed;
	
	public HttpClient(HttpClientConfig config) {
		super(config);
		
		this.connectionPool = new HttpClientConnectionPool(config.maxConnection());
		
		this.opened = false;
		this.closed = false;
	}
	
	@Override
	public void open() throws IOException {
		
		synchronized ( this ) {
			
			if ( closed ) {
				throw new IOException("Already closed");
			}
			
			if ( opened ) {
				throw new IOException("Already opened");
			}
			
			opened = true;
		}
	}
	
	@Override
	public void close() throws IOException {
		
		synchronized ( this ) {
			
			if ( closed ) {
				return ;
			}
			
			closed = true;
		}
		
		List<IOException> ioExcepts = new ArrayList<>();
		
		try {
			execServ.shutdown();
			if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
				execServ.shutdownNow();
				if ( ! execServ.awaitTermination(5L, TimeUnit.SECONDS) ) {
					ioExcepts.add(new IOException("ExecutorService#shutdownNow failed"));
				}
			}
		}
		catch ( InterruptedException ignore ) {
		}
		
		try {
			connectionPool.close();
		}
		catch ( IOException e ) {
			ioExcepts.add(e);
		}
		
		if ( ! ioExcepts.isEmpty() ) {
			throw ioExcepts.get(0);
		}
	}
	
	@Override
	public void request(HttpRequestMessagePack request)
			throws InterruptedException
			, HttpWriteMessageException
			, HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( closed ) {
				throw new HttpWriteMessageException("Already closed");
			}
			
			if ( ! opened ) {
				throw new HttpWriteMessageException("not opened");
			}
		}
		
		for ( ;; ) {
			
			try {
				writing(connectionPool.get(request.serverSocketAddress()), request);
				break;
			}
			catch ( HttpWriteMessageClosedChannelException retry ) {
			}
			catch ( IOException e ) {
				throw new HttpWriteMessageException(e);
			}
		}
	}
	
	protected void writing(
			AsynchronousSocketChannel channel
			, HttpRequestMessagePack request)
					throws InterruptedException
					, HttpWriteMessageClosedChannelException
					, HttpWriteMessageException
					, HttpMessageParseException {
		
		synchronized ( channel ) {
			HttpMessageWriter.get(channel).write(request);
			reading(channel, request);
		}
	}
	
	protected void reading(
			AsynchronousSocketChannel channel
			, HttpRequestMessagePack request) throws InterruptedException {
		
		try {
			HttpStatusLine statusLine = readHttpStatusLine(channel);
			HttpHeaderGroup headerGroup = readHttpHeaderGroup(channel);
			
			if ( request.requestLine().method() == HttpMethod.HEAD ) {
				putResponseMessagePack(new HttpResponseMessagePack(request, statusLine, headerGroup));
				return;
			}
			
			{
				Optional<String> op = headerGroup.getFieldValue(HttpHeaderField.TransferEncoding);
				
				if ( op.map(v -> v.equals("chunked")).orElse(Boolean.FALSE).booleanValue() ) {
					
					HttpMessageBody body = readHttpMessageChunkBody(channel);
					
					try {
						closeChannelIfConnectionClosed(headerGroup, channel);
					}
					catch ( IOException e ) {
						putLog(e);
					}
					
					putResponseMessagePack(new HttpResponseMessagePack(request, statusLine, headerGroup, body));
					return;
				}
			}
			
			{
				Optional<String> op = headerGroup.getFieldValue(HttpHeaderField.ContentLength);
				
				HttpMessageBody body;
				
				if ( op.isPresent() ) {
					
					try {
						int len = Integer.parseInt(op.get());
						
						if ( len < 0 ) {
							throw new HttpMessageParseException("Content-Length < 0");
						}
						
						body = readHttpMessageBytesBody(channel, len);
					}
					catch (NumberFormatException e) {
						throw new HttpMessageParseException(e);
					}
					
				} else {
					
					body = HttpMessageBody.empty();
				}
				
				try {
					closeChannelIfConnectionClosed(headerGroup, channel);
				}
				catch ( IOException e ) {
					putLog(e);
				}
				
				putResponseMessagePack(new HttpResponseMessagePack(request, statusLine, headerGroup, body));
				return;
			}
		}
		catch ( HttpMessageParseException e ) {
			this.putLog(e);
		}
		catch ( HttpReadMessageException e ) {
			
			try {
				connectionPool.closeChannel(channel);
			}
			catch ( IOException ioExcept ) {
				putLog(ioExcept);
			}
		}
		catch ( ExecutionException e ) {
			
			Throwable t = e.getCause();
			
			if ( t instanceof RuntimeException ) {
				throw (RuntimeException)t;
			}
			
			if ( t instanceof Error ) {
				throw (Error)t;
			}
			
			putLog(e);
		}
	}
	
	private HttpStatusLine readHttpStatusLine(AsynchronousSocketChannel channel)
			throws InterruptedException, HttpReadMessageException, ExecutionException {
		
		ByteBuffer buffer = ByteBuffer.allocate(1);
		CrLfLineReader crlfr = new CrLfLineReader(1024);
		
		for ( ;; ) {
			
			((Buffer)buffer).clear();
			
			Future<Integer> f = channel.read(buffer);
			
			try {
				int r = f.get().intValue();
				
				if ( r < 0 ) {
					throw new HttpReadMessageException();
				}
				
				((Buffer)buffer).flip();
				
				Optional<byte[]> op = crlfr.put(buffer);
				
				if ( op.isPresent() ) {
					return new HttpStatusLine(op.get());
				}
			}
			catch ( InterruptedException e ) {
				f.cancel(true);
				throw e;
			}
		}
	}
	
	private HttpHeaderGroup readHttpHeaderGroup(AsynchronousSocketChannel channel)
			throws InterruptedException, HttpReadMessageException, ExecutionException {
		
		ByteBuffer buffer = ByteBuffer.allocate(1);
		CrLfLineReader crlfr = new CrLfLineReader(1024);
		List<String> lines = new ArrayList<>();
		
		for ( ;; ) {
			
			((Buffer)buffer).clear();
			
			Future<Integer> f = channel.read(buffer);
			
			try {
				int r = f.get().intValue();
				
				if ( r < 0 ) {
					throw new HttpReadMessageException();
				}
				
				((Buffer)buffer).flip();
				
				Optional<byte[]> op = crlfr.put(buffer);
				
				if ( op.isPresent() ) {
					
					byte[] bs = op.get();
					
					if ( bs.length > 0 ) {
						
						lines.add(new String(bs, StandardCharsets.US_ASCII));
						
					} else {
						
						return HttpHeaderGroup.lines(lines);
					}
				}
			}
			catch ( InterruptedException e ) {
				f.cancel(true);
				throw e;
			}
		}
	}
	
	private HttpMessageBody readHttpMessageChunkBody(AsynchronousSocketChannel channel)
			throws InterruptedException, HttpReadMessageException, HttpMessageParseException, ExecutionException {
		
		HttpMessageChunkBodyReader reader = new HttpMessageChunkBodyReader();
		ByteBuffer buffer = ByteBuffer.allocate(1);
		
		for ( ;; ) {
			
			Future<Integer> f = channel.read(buffer);
			
			try {
				int r = f.get().intValue();
				
				if ( r < 0 ) {
					throw new HttpReadMessageException();
				}
				
				if ( reader.put(buffer) ) {
					return reader.getHttpMessageBody();
				}
			}
			catch ( InterruptedException e ) {
				f.cancel(true);
				throw e;
			}
		}
	}
	
	private HttpMessageBody readHttpMessageBytesBody(AsynchronousSocketChannel channel, int contentLength)
			throws InterruptedException, HttpReadMessageException, HttpMessageParseException, ExecutionException {
		
		ByteBuffer buffer = ByteBuffer.allocate(contentLength);
		
		for ( ;; ) {
			
			Future<Integer> f = channel.read(buffer);
			
			try {
				int r = f.get().intValue();
				
				if ( r < 0 ) {
					throw new HttpReadMessageException();
				}
				
				if ( ! buffer.hasRemaining() ) {
					
					((Buffer)buffer).flip();
					
					HttpMessageBytesBodyReader reader = new HttpMessageBytesBodyReader(contentLength);
					
					if ( reader.put(buffer) ) {
						
						return reader.getHttpMessageBody();
						
					} else {
						
						throw new HttpReadMessageException();
					}
				}
			}
			catch ( InterruptedException e ) {
				f.cancel(true);
				throw e;
			}
			
		}
	}

	private void closeChannelIfConnectionClosed(
			HttpHeaderGroup headerGroup
			, AsynchronousSocketChannel channel)
					throws HttpMessageParseException, IOException {
		
		Optional<String> op = headerGroup.getFieldValue(HttpHeaderField.Connection);
		
		if ( op.map(v -> v.equalsIgnoreCase("close")).orElse(Boolean.FALSE).booleanValue() ) {
			
			connectionPool.closeChannel(channel);
		}
	}
	
}
