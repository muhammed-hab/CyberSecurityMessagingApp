package server;

public interface ServerMessageHandler {

    /**
     * Handles an incoming message for the server and returns a response.
     * @param message The message received from a client
     * @return The message to return to the client.
     */
    byte[] handleMessage(byte[] message);

}
