package test;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.shimizukenta.http.HttpVersion1p1AsynchronousSocketChannelServer;
import com.shimizukenta.http.HttpVersion1p1AsynchronousSocketChannelServerConfig;

public class Test2 {

	public Test2() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		HttpVersion1p1AsynchronousSocketChannelServerConfig config = new HttpVersion1p1AsynchronousSocketChannelServerConfig();
		
		config.addBind(new InetSocketAddress("127.0.0.1", 8080));
		
		try (
				HttpVersion1p1AsynchronousSocketChannelServer server = new HttpVersion1p1AsynchronousSocketChannelServer(config);
				) {
			
			server.addLogListener(log -> {echo(log);});
//			server.addRequestMessageLogListener(log -> {echo(log);});
//			server.addResponseMessageLogListener(log -> {echo(log);});
			server.addAccessLogListener(log -> {echo(log);});
			
			server.open();
			
			synchronized ( Test2.class ) {
				Test2.class.wait();
			}
		}
		catch ( IOException e ) {
			echo(e);
		}
		catch ( InterruptedException ignore ) {
		}
		
	}
	
	private static final Object syncEcho = new Object();
	
	private static void echo(Object o) {
		
		synchronized ( syncEcho ) {
			
			if ( o instanceof Throwable ) {
				((Throwable) o).printStackTrace();
			} else {
				System.out.println(o);
			}
			System.out.println();
		}
	}

}
