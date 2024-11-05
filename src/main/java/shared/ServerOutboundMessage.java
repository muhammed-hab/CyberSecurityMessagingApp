package shared;

public sealed interface ServerOutboundMessage {

    record Handshake(long serverVersion) implements ServerOutboundMessage {}

    record IncompatibleVersions() implements ServerOutboundMessage {}
    record ServerError(String error) implements ServerOutboundMessage {}

    record RecentMessages(Message[] messages) implements ServerOutboundMessage {}

    record ConversationList(String[] with) implements ServerOutboundMessage {}

    record InvalidLogin() implements ServerOutboundMessage {}
    record InvalidSessionToken() implements ServerOutboundMessage {}
    record SuccessfulLogin(String sessionToken) implements ServerOutboundMessage {}

    record AccountCreated(String sessionToken) implements ServerOutboundMessage {}
    record AccountAlreadyExists() implements ServerOutboundMessage {}

    record MessageSent() implements ServerOutboundMessage {}

}
