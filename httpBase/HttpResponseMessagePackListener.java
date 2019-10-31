package httpBase;

import java.util.EventListener;

public interface HttpResponseMessagePackListener extends EventListener {
	public void receive(HttpResponseMessagePack msgPack);
}
