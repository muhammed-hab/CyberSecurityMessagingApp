package server;

import javax.net.ssl.SSLContext;

public interface SecureServer {

    /**
     * Starts a secure SSL server with the specified parameters.
     * @param host The host to host on.
     * @param port The port to host on.
     * @param sslContext The SSL Certificate and so on to use.
     * @param onMessageReceived The callback for when an incoming message is received.
     * @throws Exception If there is an issue with the server.
     */
    void startServer(String host, int port, SSLContext sslContext, ServerMessageHandler onMessageReceived) throws Exception;

}
