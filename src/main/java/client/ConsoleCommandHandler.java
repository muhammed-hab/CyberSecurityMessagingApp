package client;

public class ConsoleCommandHandlerImpl implements ConsoleCommandHandler {

    private final ConsoleCommandParser parser;
    private String sessionToken;  // Stores the session token for logged-in users

    public ConsoleCommandHandlerImpl(ConsoleCommandParser parser) {
        this.parser = parser;
    }

    @Override
    public String handleConsoleCommand(String input) {
        try {
            // Parse the input command
            ConsoleCommand command = parser.parseCommand(input);

            // Handle the command based on its type
            if (command instanceof ConsoleCommand.LogIn logIn) {
                return handleLogIn(logIn.username(), logIn.password());
            } else if (command instanceof ConsoleCommand.GetConversations) {
                return requireLogin() ? "Listing conversations..." : "Please log in first.";
            } else if (command instanceof ConsoleCommand.GetMessagesWith getMessagesWith) {
                return requireLogin() ? "Getting messages with " + getMessagesWith.username() : "Please log in first.";
            } else if (command instanceof ConsoleCommand.SendMessage sendMessage) {
                return requireLogin() ? "Sending message to " + sendMessage.toUser() + ": " + sendMessage.message() : "Please log in first.";
            } else if (command instanceof ConsoleCommand.CreateAccount createAccount) {
                return handleCreateAccount(createAccount.username(), createAccount.password());
            } else if (command instanceof ConsoleCommand.CheckConnection) {
                return "Checking connection... Connected!";
            } else {
                return "Unknown command.";
            }

        } catch (ConsoleCommandParser.InvalidCommandError e) {
            return "Invalid command format.";
        }
    }

    private String handleLogIn(String username, String password) {
        if ("user".equals(username) && "pass".equals(password)) {  // Example login validation
            sessionToken = "TOKEN123";  // Set a session token after successful login
            return "Logged in as " + username;
        } else {
            return "Invalid username or password.";
        }
    }

    private String handleCreateAccount(String username, String password) {
        // Example of creating a new account, typically you'd add to a user database here
        return "Account created for user: " + username;
    }

    private boolean requireLogin() {
        return sessionToken != null;
    }

    public void logout() {
        sessionToken = null;  // Clears the session token to log out the user
    }
}
