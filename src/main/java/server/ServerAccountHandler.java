package server;

import shared.Message;

public interface ServerAccountHandler {

    class AccountAlreadyExistsException extends Exception {}

    /**
     * Creates the specified account.
     * @param username The account's username.
     * @param password The account's password.
     * @throws AccountAlreadyExistsException If an account with the username exists.
     * @throws Exception If there is an error creating the account.
     */
    void createAccount(String username, String password) throws Exception;

    class AccountDoesNotExistException extends Exception {}

    /**
     * Saves a message between two actors.
     * @param message The message.
     * @throws AccountDoesNotExistException If one of the accounts does not exist.
     * @throws Exception If there is an error saving the message.
     */
    void saveMessage(Message message) throws Exception;

    /**
     * Gets a list of all conversations for the user.
     * @param userID The user to get conversations for.
     * @throws AccountDoesNotExistException If the account does not exist.
     * @throws Exception If there is another error.
     * @return A list of usernames the account has conversations with.
     */
    String[] getConversations(String userID) throws Exception;

    /**
     * Gets a list of messages between two actors.
     * @param accountID1 The first party of the conversation.
     * @param accountID2 The second party of the conversation.
     * @throws AccountDoesNotExistException If one of the accounts do not exist.
     * @throws Exception If there is an error getting messages.
     */
    Message[] getMessages(String accountID1, String accountID2) throws Exception;

    class TokenExpiredException extends Exception {}
    class InvalidTokenException extends Exception {}
    /**
     * Attempts to look up an account by using the session token.
     * @param sessionToken The token to look up
     * @return The account ID
     * @throws TokenExpiredException If the token has expired.
     * @throws InvalidTokenException If the token cannot be looked up.
     * @throws Exception If there is an error looking up the token.
     */
    String getAccountID(String sessionToken) throws Exception;

    class InvalidLoginException extends Exception {}
    /**
     * Creates a session token for the user.
     * @param username The username
     * @param password The password
     * @return The session token
     * @throws InvalidLoginException If the username and password do not match.
     * @throws AccountDoesNotExistException If the account does not exist.
     * @throws Exception If there is an error creating.
     */
    String getSessionToken(String username, String password) throws Exception;

    /**
     * Saves a log
     * @param message The message
     * @param time The time of the message
     * @throws Exception If the message could not be logged.
     */
    void insertLog(String message, long time) throws Exception;

}
