package client;

public sealed interface ConsoleCommand {

    record LogIn(String username, String password) implements ConsoleCommand {}
    record GetConversations() implements ConsoleCommand {}
    record GetMessagesWith(String username) implements ConsoleCommand {}
    record SendMessage(String toUser, String message) implements ConsoleCommand {}
    record CreateAccount(String username, String password) implements ConsoleCommand {}
    record CheckConnection() implements ConsoleCommand {}

}
