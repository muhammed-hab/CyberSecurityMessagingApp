package server;

import javax.net.ssl.SSLContext;

public class SSLServer implements SecureServer {
    @Override
    public void startServer(String host, int port, SSLContext sslContext, ServerMessageHandler onMessageReceived) throws Exception {
        throw new RuntimeException("Not implemented");
    }
}
