package httpBase;

public abstract class HttpBodyReader {
	
	public HttpBodyReader() {
		// TODO Auto-generated constructor stub
	}
	
	abstract public void put(byte[] bs);
	abstract public boolean completed();
	abstract public byte[] value();
}
