package example1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import http.util.HttpServer;
import http.util.HttpServerConfig;

public class ExampleHttpGeneralServer {

	public ExampleHttpGeneralServer() {
		/* Nothing */
	}
	
	public static void main(String[] args) {
		
		String hostName = "HTTP-GENERAL-SERVER";
		
		HttpServerConfig config = new HttpServerConfig();
		
		config.serverAddress(new InetSocketAddress("127.0.0.1", 80));
		
		config.generalFileServerServiceConfig().serverRoot(Paths.get("/path/to/root-directory"));
		config.generalFileServerServiceConfig().directoryIndex("index.html");
		config.generalFileServerServiceConfig().hostName(hostName);
		
		try (
				HttpServer server = HttpServer.open(config);
				) {
			
			server.addAccessLogListener(System.out::println);
			server.addResponseLogListener(System.out::println);
			server.addLogListener(System.out::println);
			
			synchronized ( ExampleHttpGeneralServer.class ) {
				ExampleHttpGeneralServer.class.wait();
			}
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
	}

}
