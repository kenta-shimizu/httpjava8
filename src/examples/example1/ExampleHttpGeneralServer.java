package example1;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Paths;

import com.shimizukenta.http.GeneralFileHttpVersion1p1ServerService;
import com.shimizukenta.http.GeneralFileHttpVersion1p1ServerServiceConfig;
import com.shimizukenta.http.HttpServer;
import com.shimizukenta.http.HttpVersion1p1AsynchronousSocketChannelServer;
import com.shimizukenta.http.HttpVersion1p1AsynchronousSocketChannelServerConfig;

public class ExampleHttpGeneralServer {

	public ExampleHttpGeneralServer() {
		/* Nothing */
	}
	
	public static void main(String[] args) {
		
		try {
			
			SocketAddress serverSocketAddress = new InetSocketAddress("127.0.0.1", 8080);
			String serverName = "HTTP-GENERAL-SERVER";
			String pathToRootDirectory = "/path/to/root-directory";
			String directoryIndex = "index.html";
			
			
			final HttpVersion1p1AsynchronousSocketChannelServerConfig serverConfig = new HttpVersion1p1AsynchronousSocketChannelServerConfig();
			serverConfig.addBind(serverSocketAddress);
			
			final GeneralFileHttpVersion1p1ServerServiceConfig generalFileConfig = new GeneralFileHttpVersion1p1ServerServiceConfig();
			generalFileConfig.serverName(serverName);
			generalFileConfig.serverRoot(Paths.get(pathToRootDirectory));
			generalFileConfig.addDirectoryIndexFile(directoryIndex);
			
			
			try (
					HttpServer server = new HttpVersion1p1AsynchronousSocketChannelServer(serverConfig);
					) {
				
				server.addServerService(new GeneralFileHttpVersion1p1ServerService(generalFileConfig));
				
				server.addLogListener(log -> {echo(log);});
				server.addRequestMessageLogListener(log -> {echo(log);});
				server.addResponseMessageLogListener(log -> {echo(log);});
				server.addAccessLogListener(log -> {echo(log);});
				
				server.open();
				
				synchronized ( ExampleHttpGeneralServer.class ) {
					ExampleHttpGeneralServer.class.wait();
				}
			}
			catch ( InterruptedException ignore ) {
			}
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
