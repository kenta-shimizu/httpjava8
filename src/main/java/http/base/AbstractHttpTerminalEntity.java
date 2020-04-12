package http.base;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import com.shimizukenta.http.HttpLog;
import com.shimizukenta.http.HttpLogListener;

public abstract class AbstractHttpTerminalEntity implements Closeable {
	
	private boolean opened;
	private boolean closed;
	
	public AbstractHttpTerminalEntity() {
		opened = false;
		closed = false;
	}
	
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
	}
	
	public boolean isOpen() {
		synchronized ( this ) {
			return opened && ! closed;
		}
	}
	
	protected boolean isClosed() {
		synchronized ( this ) {
			return closed;
		}
	}
	
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
