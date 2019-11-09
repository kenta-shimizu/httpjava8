package httpUtil;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import httpBase.AbstractHttpServer;

public class HttpServer extends AbstractHttpServer {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	public HttpServer(HttpServerConfig config) {
		super(config);
	}
	
	@Override
	public void open() throws IOException {
		
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void close() throws IOException {
		
		// TODO Auto-generated method stub
		
		//execServ;
		
	}

}
