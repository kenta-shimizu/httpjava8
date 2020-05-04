package test;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.shimizukenta.http.GeneralFileHttpVersion1p1ServerService;
import com.shimizukenta.http.GeneralFileHttpVersion1p1ServerServiceConfig;
import com.shimizukenta.http.HttpVersion1p1AsynchronousSocketChannelServer;
import com.shimizukenta.http.HttpVersion1p1AsynchronousSocketChannelServerConfig;

public class Test2 {

	public Test2() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		final HttpVersion1p1AsynchronousSocketChannelServerConfig serverConfig = new HttpVersion1p1AsynchronousSocketChannelServerConfig();
		serverConfig.addBind(new InetSocketAddress("127.0.0.1", 8080));
		
		final GeneralFileHttpVersion1p1ServerServiceConfig generalFileConfig = new GeneralFileHttpVersion1p1ServerServiceConfig();
		generalFileConfig.serverName("General-Http-Server");
//		generalFileConfig.serverRoot(Paths.get("/var/www/html"));
		generalFileConfig.serverRoot(Paths.get("/Users/shimizukenta/Documents/html"));
		generalFileConfig.addDirectoryIndexFile("index.html");
		
		
		try (
				HttpVersion1p1AsynchronousSocketChannelServer server = new HttpVersion1p1AsynchronousSocketChannelServer(serverConfig);
				) {
			
			server.addServerService(new GeneralFileHttpVersion1p1ServerService(generalFileConfig));
			
			server.addLogListener(log -> {echo(log);});
			server.addRequestMessageLogListener(log -> {echo(log);});
			server.addResponseMessageLogListener(log -> {echo(log);});
			server.addAccessLogListener(log -> {echo(log);});
			
			server.open();
			
			synchronized ( Test2.class ) {
				Test2.class.wait();
			}
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( Throwable t ) {
			echo(t);
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
