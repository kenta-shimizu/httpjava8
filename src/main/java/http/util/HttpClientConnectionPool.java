package http.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.shimizukenta.http.HttpLog;
import com.shimizukenta.http.HttpLogListener;

import http.base.HttpResponseMessagePack;
import http.base.HttpResponseMessagePackListener;

public class HttpClientConnectionPool implements Closeable {
	
	private ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final HttpResponseMessagePackListener reponseMsgListener = rspMsg -> {
		putResponseMessagePack(rspMsg);
	};
	
	private final HttpLogListener logListener = log -> {
		putLog(log);
	};
	
	private final Map<SocketAddress, HttpClientConnection[]> map = new HashMap<>();
	
	private final AtomicInteger autoNumber = new AtomicInteger(0);
	
	private final int maxConnection;
	
	private boolean closed;
	
	public HttpClientConnectionPool(int maxConnect) {
		this.maxConnection = maxConnect;
		this.closed = false;
	}
	
	public HttpClientConnection get(SocketAddress addr)
			throws IOException, InterruptedException {
		
		synchronized ( this ) {
			
			if ( closed ) {
				throw new IOException("Already closed");
			}
			
			final int autoNum = getIndex();
			
			HttpClientConnection[] connections = map.computeIfAbsent(addr, x -> {
				
				HttpClientConnection[] vv = new HttpClientConnection[maxConnection];
				
				for ( int i = 0, m = vv.length; i < m; ++i ) {
					vv[i] = null;
				}
				
				return vv;
			});
			
			HttpClientConnection connection = connections[autoNum];
			
			if ( connection != null ) {
				
				if ( connection.isOpen() ) {
					
					return connection;
					
				} else {
					
					closeConnection(connection);
				}
			}
			
			connection = HttpClientConnection.get(addr, reponseMsgListener, execServ);
			connection.addLogListener(logListener);
			connections[autoNum] = connection;
			return connection;
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
			
			map.clear();
			
			if ( ioExcept != null ) {
				throw ioExcept;
			}
		}
	}
	
	public void closeConnection(HttpClientConnection connection) throws IOException {
		
		synchronized ( this ) {
			
			for ( SocketAddress addr : map.keySet() ) {
				
				HttpClientConnection[] connections = map.get(addr);
				
				for ( int i = 0, m = connections.length; i < m ; ++i ) {
					
					if ( Objects.equals(connections[i], connection) ) {
						
						connections[i] = null;
						
						connection.close();
						
						break;
					}
				}
				
				if ( Stream.of(connections)
						.allMatch(c -> c == null ? true : (! c.isOpen()))
						) {
					
					map.remove(addr);
				}
			}
		}
	}
	
	
	/*** Response Listener ***/
	private final Collection<HttpResponseMessagePackListener> rspMsgPackListeners = new CopyOnWriteArrayList<>();
	
	public boolean addResponseMessagePackListener(HttpResponseMessagePackListener lstnr) {
		return rspMsgPackListeners.add(lstnr);
	}
	
	public boolean removeResponseMessagePackListener(HttpResponseMessagePackListener lstnr) {
		return rspMsgPackListeners.remove(lstnr);
	}
	
	protected void putResponseMessagePack(HttpResponseMessagePack msgPack) {
		rspMsgPackListeners.forEach(lstnr -> {
			lstnr.receive(msgPack);
		});
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
