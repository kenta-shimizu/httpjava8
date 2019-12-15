package http.util;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import http.HttpMessageParseException;
import http.HttpWriteMessageClosedChannelException;
import http.HttpWriteMessageException;
import http.base.AbstractHttpClient;
import http.base.HttpRequestMessagePack;

public class HttpClient extends AbstractHttpClient {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final HttpClientConnectionPool connectionPool;
	
	public HttpClient(HttpClientConfig config) {
		super(config);
		
		this.connectionPool = new HttpClientConnectionPool(config.maxConnection());
		
		this.connectionPool.addResponseMessagePackListener(rspMsg -> {
			putResponseMessagePack(rspMsg);
		});
		
		this.connectionPool.addLogListener(log -> {
			putLog(log);
		});
	}
	
	@Override
	public void close() throws IOException {
		
		IOException ioExcept = null;
		
		synchronized ( this ) {
			
			try {
				super.close();
			}
			catch ( IOException e ) {
				ioExcept = e;
			}
			
			if ( isClosed() ) {
				return;
			}
		}
		
		try {
			execServ.shutdown();
			if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS) ) {
				execServ.shutdownNow();
				if ( ! execServ.awaitTermination(5L, TimeUnit.SECONDS) ) {
					ioExcept = new IOException("ExecutorService#shutdownNow failed");
				}
			}
		}
		catch ( InterruptedException ignore ) {
		}
		
		try {
			connectionPool.close();
		}
		catch ( IOException e ) {
			ioExcept = e;
		}
		
		if ( ioExcept != null ) {
			throw ioExcept;
		}
	}
	
	@Override
	public void request(HttpRequestMessagePack request)
			throws InterruptedException
			, HttpWriteMessageException
			, HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( isClosed() ) {
				throw new HttpWriteMessageException("Already closed");
			}
			
			if ( ! isOpen() ) {
				throw new HttpWriteMessageException("not opened");
			}
		}
		
		for ( ;; ) {
			
			try {
				HttpClientConnection connection = connectionPool.get(request.serverSocketAddress());
				
				try {
					connection.request(request);
					break;
				}
				catch (HttpWriteMessageClosedChannelException retry) {
					connectionPool.closeConnection(connection);
					continue;
				}
			}
			catch ( IOException e ) {
				throw new HttpWriteMessageException(e);
			}
		}
	}
	
}
