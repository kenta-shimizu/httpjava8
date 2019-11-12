package http.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import http.base.AbstractHttpClient;
import http.base.HttpMessageParseException;
import http.base.HttpRequestMessagePack;
import http.base.HttpWriteMessageClosedChannelException;
import http.base.HttpWriteMessageException;

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
		
		this.connectionPool.addResponseMessagePackListener(rspMsg -> {
			putResponseMessagePack(rspMsg);
		});
		
		this.connectionPool.addLogListener(log -> {
			putLog(log);
		});
		
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
