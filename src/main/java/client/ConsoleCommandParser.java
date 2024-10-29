package client;

/**
 * Parses commands from the user.
 * Commands are case-insensitive, but the parameters are not.
 * Most parts are delimited by spaces.
 * Command formats are:
 * <pre>
 * {@link client.ConsoleCommand.LogIn}
 *      login [username] [password]
 * {@link client.ConsoleCommand.GetConversations}
 *      getConversations
 * {@link client.ConsoleCommand.GetMessagesWith}
 *      getMessages [with username]
 * {@link client.ConsoleCommand.SendMessage}
 *      send [to username] [message - to end of line]
 * {@link client.ConsoleCommand.CreateAccount}
 *      create [username] [password]
 * {@link client.ConsoleCommand.CheckConnection}
 *      checkConnection
 * </pre>
 */
public interface ConsoleCommandParser {

    class InvalidCommandError extends Exception {}

    /**
     * Parses the inputted string and returns the relevant console command.
     * @param command The command to parse
     * @return The parsed command
     * @throws InvalidCommandError If the command does not fit the relevant format.
     */
    ConsoleCommand parseCommand(String command) throws InvalidCommandError;

}
