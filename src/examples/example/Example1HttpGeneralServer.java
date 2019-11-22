package example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import http.util.HttpServer;
import http.util.HttpServerConfig;

public class Example1HttpGeneralServer {

	public Example1HttpGeneralServer() {
		/* Nothing */
	}
	
	public static void main(String[] args) {
		
		HttpServerConfig config = new HttpServerConfig();
		
		config.serverAddress(new InetSocketAddress("127.0.0.1", 80));
		config.generalFileServerServiceConfig().serverRoot(Paths.get("/path/to/root-directory"));
		config.generalFileServerServiceConfig().directoryIndex("index.html");
		config.generalFileServerServiceConfig().hostName("GENERAL-FILE-SERVER");
		
		try (
				HttpServer server = new HttpServer(config);
				) {
			
			server.addAccessLogListener(System.out::println);
			server.addResponseLogListener(System.out::println);
			server.addLogListener(System.out::println);
			
			server.open();
			
			synchronized ( Example1HttpGeneralServer.class ) {
				Example1HttpGeneralServer.class.wait();
			}
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
	}

}
