package client;

import client.ConsoleCommand.*;

/**
 * Implementation of the ConsoleCommandParser interface.
 * This class parses input strings and returns the appropriate ConsoleCommand.
 */
public class ConsoleCommandParserImpl implements ConsoleCommandParser {

    @Override
    public ConsoleCommand parseCommand(String command) throws InvalidCommandError {
        String[] parts = command.trim().split(" ", 2); // Split into command and arguments
        String action = parts[0].toLowerCase();        // Commands are case-insensitive

        switch (action) {
            case "login":
                return parseLogIn(parts);
            case "getconversations":
                return parseGetConversations(parts);
            case "getmessages":
                return parseGetMessagesWith(parts);
            case "send":
                return parseSendMessage(parts);
            case "create":
                return parseCreateAccount(parts);
            case "checkconnection":
                return parseCheckConnection(parts);
            default:
                throw new InvalidCommandError();
        }
    }

    private ConsoleCommand parseLogIn(String[] parts) throws InvalidCommandError {
        if (parts.length < 2) throw new InvalidCommandError();
        String[] credentials = parts[1].split(" ");
        if (credentials.length != 2) throw new InvalidCommandError();
        return new LogIn(credentials[0], credentials[1]);
    }

    private ConsoleCommand parseGetConversations(String[] parts) throws InvalidCommandError {
        if (parts.length != 1) throw new InvalidCommandError();
        return new GetConversations();
    }

    private ConsoleCommand parseGetMessagesWith(String[] parts) throws InvalidCommandError {
        if (parts.length < 2 || !parts[1].startsWith("with ")) throw new InvalidCommandError();
        String username = parts[1].substring(5); // Extracts username after "with "
        return new GetMessagesWith(username);
    }

    private ConsoleCommand parseSendMessage(String[] parts) throws InvalidCommandError {
        if (parts.length < 2) throw new InvalidCommandError();
        String[] messageParts = parts[1].split(" ", 2);
        if (messageParts.length != 2) throw new InvalidCommandError();
        return new SendMessage(messageParts[0], messageParts[1]);
    }

    private ConsoleCommand parseCreateAccount(String[] parts) throws InvalidCommandError {
        if (parts.length < 2) throw new InvalidCommandError();
        String[] credentials = parts[1].split(" ");
        if (credentials.length != 2) throw new InvalidCommandError();
        return new CreateAccount(credentials[0], credentials[1]);
    }

    private ConsoleCommand parseCheckConnection(String[] parts) throws InvalidCommandError {
        if (parts.length != 1) throw new InvalidCommandError();
        return new CheckConnection();
    }
}

