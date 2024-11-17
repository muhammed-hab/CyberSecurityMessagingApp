package server;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SecureServerImpl implements SecureServer {

    private SSLServerSocket serverSocket;
    private boolean isRunning;

    @Override
    public void startServer(String host, int port, SSLContext sslContext, ServerMessageHandler onMessageReceived) throws Exception {
        // Use the SSLContext to create an SSLServerSocketFactory
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

        System.out.println("SSL Server started on " + host + ":" + port);

        isRunning = true;

        while (isRunning) {
            // Accept client connections
            try (Socket clientSocket = serverSocket.accept()) {

                System.out.println("Client connected");

                // Read message from client
                byte[] message = clientSocket.getInputStream().readAllBytes();
                if (message != null) {
                    System.out.println("Received: " + message);

                    // Pass the message to the handler and capture response
                    byte[] response = onMessageReceived.handleMessage(message);

                    // Send response back to the client
                    clientSocket.getOutputStream().write(response);
                }
            } catch (Exception e) {
                System.err.println("Error handling client connection: " + e.getMessage());
            }
        }
    }

    /**
     * Stops the server.
     */
    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("SSL Server stopped.");
        } catch (Exception e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }
}

/* example usage:
SSLContext sslContext = // obtain SSLContext
SecureServer server = new SecureServerImpl();
ServerMessageHandler handler = message -> "Received: " + message;

server.startServer("localhost", 8443, sslContext, handler); */

