package client;
import java.util.HashMap;
import java.util.Map;

/**
 * This interface should be responsible for managing session tokens and informing the user if they are not logged in.
 */
public interface ConsoleCommandHandler {

    /**
     * Takes the input from the user and returns what should be outputted to the user.
     * @param input the user's input
     * @return output to send to the user
     */
    String handleConsoleCommand(String input);

    /**
     * Logs in the user by generating and storing a session token.
     * @param userId the ID of the user
     * @return session token for the user
     */
    String loginUser(String userId);

    /**
     * Logs out the user by removing their session token.
     * @param userId the ID of the user
     * @return true if the user was logged out successfully, false otherwise
     */
    boolean logoutUser(String userId);

    /**
     * Checks if the user is logged in by verifying their session token.
     * @param userId the ID of the user
     * @return true if the user is logged in, false otherwise
     */
    boolean isUserLoggedIn(String userId);
}

// Implementation class
class ConsoleCommandHandlerImpl implements ConsoleCommandHandler {

    private Map<String, String> sessionTokens = new HashMap<>();

    @Override
    public String handleConsoleCommand(String input) {
        String[] commandParts = input.split(" ");
        String command = commandParts[0];
        
        switch (command) {
            case "login":
                if (commandParts.length > 1) {
                    String userId = commandParts[1];
                    return loginUser(userId);
                } else {
                    return "Error: User ID is required to log in.";
                }
                
            case "logout":
                if (commandParts.length > 1) {
                    String userId = commandParts[1];
                    if (logoutUser(userId)) {
                        return "User " + userId + " logged out successfully.";
                    } else {
                        return "Error: User " + userId + " is not logged in.";
                    }
                } else {
                    return "Error: User ID is required to log out.";
                }
                
            case "status":
                if (commandParts.length > 1) {
                    String userId = commandParts[1];
                    return isUserLoggedIn(userId) ? "User " + userId + " is logged in." : "User " + userId + " is not logged in.";
                } else {
                    return "Error: User ID is required to check status.";
                }
                
            default:
                return "Error: Unknown command.";
        }
    }

    @Override
    public String loginUser(String userId) {
        String sessionToken = generateSessionToken(userId);
        sessionTokens.put(userId, sessionToken);
        return "User " + userId + " logged in with token: " + sessionToken;
    }

    @Override
    public boolean logoutUser(String userId) {
        return sessionTokens.remove(userId) != null;
    }

    @Override
    public boolean isUserLoggedIn(String userId) {
        return sessionTokens.containsKey(userId);
    }

    private String generateSessionToken(String userId) {
        return "TOKEN-" + userId + "-" + System.currentTimeMillis();
    }
}
