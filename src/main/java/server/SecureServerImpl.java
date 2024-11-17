package server;

import shared.ByteParsers;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SecureServerImpl implements SecureServer {

    private ServerSocket serverSocket;
    private boolean isRunning;

    @Override
    public void startServer(String host, int port, SSLContext sslContext, ServerMessageHandler onMessageReceived) throws Exception {
        // Use the SSLContext to create an SSLServerSocketFactory
        var sslServerSocketFactory = sslContext.getServerSocketFactory();
        serverSocket = sslServerSocketFactory.createServerSocket(port, 20, InetAddress.getByName(host));
//        serverSocket = new ServerSocket(port, 20, InetAddress.getByName(host));

        System.out.println("SSL Server started on " + host + ":" + port);

        isRunning = true;

        while (isRunning) {
            // Accept client connections
            try (Socket clientSocket = serverSocket.accept()) {

                System.out.println("Client connected");

                // Read message from client
                int msgLen = ByteParsers.bytesToInt(clientSocket.getInputStream().readNBytes(4));
                System.out.println(msgLen);
                byte[] message = clientSocket.getInputStream().readNBytes(msgLen);

                // Pass the message to the handler and capture response
                byte[] response = onMessageReceived.handleMessage(message);

                // Send response back to the client
                clientSocket.getOutputStream().write(ByteParsers.intToBytes(response.length));
                clientSocket.getOutputStream().write(response);
                clientSocket.getOutputStream().flush();
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

