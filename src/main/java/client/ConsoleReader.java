package client;

public interface ConsoleReader {

    /**
     * Listens to stdin and stdout for user commands.
     * @param callback the callback to use whenever a command is inputted
     * @throws Exception if an error occurs while reading
     */
    void listenForConsole(ConsoleCommandHandler callback) throws Exception;

}
