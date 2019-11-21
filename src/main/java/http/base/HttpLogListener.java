package http.base;

import java.util.EventListener;

public interface HttpLogListener extends EventListener {
	public void receive(HttpLog log);
}
