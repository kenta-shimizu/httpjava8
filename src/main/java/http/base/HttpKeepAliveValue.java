package http.base;

public class HttpKeepAliveValue {
	
	private final long timeout;
	private int remaining;
	
	public HttpKeepAliveValue(long timeout, int remaining) {
		this.timeout = timeout;
		this.remaining = remaining;
	}
	
	public int remaining() {
		synchronized ( this ) {
			return remaining;
		}
	}
	
	public int decreaseRemaining() {
		synchronized ( this ) {
			remaining -= 1;
			return remaining;
		}
	}
	
	public long timeout() {
		return timeout;
	}
	
}
