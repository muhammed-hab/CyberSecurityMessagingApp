package client;

public interface ServerInterface {

    /**
     * sends a message to the server and returns its output
     * @param message the message to deliver
     * @return the output from the server
     * @throws Exception if there is an issue with communicating with the server
     */
    byte[] sendServerCommand(byte[] message) throws Exception;

}
