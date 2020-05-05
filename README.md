# httpjava8

## Introduction

This library is template of Http Server that runs only on Java8+.  
Use to create simple API HTTP Server.


## for HTTP Server

### Open General HTTP Server

```
    final HttpVersion1p1AsynchronousSocketChannelServerConfig serverConfig = new HttpVersion1p1AsynchronousSocketChannelServerConfig();
    serverConfig.addBind(new InetSocketAddress("127.0.0.1", 8080));

    final GeneralFileHttpVersion1p1ServerServiceConfig generalFileConfig = new GeneralFileHttpVersion1p1ServerServiceConfig();
    generalFileConfig.serverName("HTTP-GENERAL-SERVER");
    generalFileConfig.serverRoot(Paths.get("/path/to/root-directory"));
    generalFileConfig.addDirectoryIndexFile("index.html");
                
    HttpServer server = new HttpVersion1p1AsynchronousSocketChannelServer(serverConfig);

    server.addServerService(new GeneralFileHttpVersion1p1ServerService(generalFileConfig));

    server.open();
```

See also ["/src/examples/example1/ExampleHttpGeneralServer.java"](/src/examples/example1/)


### Create API HTTP Server

Implements ```HttpServerService```  
See ["/src/examples/example2/Example2.java"](/src/examples/example2/)


## for HTTP  Client

Use "java.net.http.HttpClient" on Java11+ or other library.

