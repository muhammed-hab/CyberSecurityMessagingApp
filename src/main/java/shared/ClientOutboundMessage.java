package shared;

public sealed interface ClientOutboundMessage {

    record Handshake(long version) implements ClientOutboundMessage {}
    record LogIn(String username, String password) implements ClientOutboundMessage {}
    record GetMessages(String sessionToken, String userId) implements ClientOutboundMessage {}
    record GetConversations(String sessionToken) implements ClientOutboundMessage {}
    record SendMessage(String sessionToken, String to, String message) implements ClientOutboundMessage {}
    record CreateAccount(String username, String password) implements ClientOutboundMessage {}

}
