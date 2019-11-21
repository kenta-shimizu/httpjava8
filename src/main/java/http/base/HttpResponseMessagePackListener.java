package http.base;

import java.util.EventListener;

public interface HttpResponseMessagePackListener extends EventListener {
	public void receive(HttpResponseMessagePack msgPack);
}
