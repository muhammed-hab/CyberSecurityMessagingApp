package client;

/**
 * This interface should be responsible for managing session tokens and informing the user if they are not logged in.
 */
public interface ConsoleCommandHandler {

    /**
     * takes the input from the user and returns what should be outputted to the user.
     * @param input the user's input
     * @return output to send to the user
     */
    String handleConsoleCommand(String input);

}
