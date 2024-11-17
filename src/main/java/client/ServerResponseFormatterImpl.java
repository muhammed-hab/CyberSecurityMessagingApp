package client;

import shared.ServerOutboundMessage;

public class ServerResponseFormatterImpl implements ServerResponseFormatter {


    @Override
    public String formatResponse(ServerOutboundMessage message) {
        return switch (message) {
            case ServerOutboundMessage.AccountAlreadyExists accountAlreadyExists -> "An account already exists";
            case ServerOutboundMessage.AccountCreated accountCreated -> "Account is created";
            case ServerOutboundMessage.ConversationList conversationList -> {
                String output = "";
                for (String user : conversationList.with()) {
                    output += "Talking to " + user + "\n";
                }
                yield output;
            }
            case ServerOutboundMessage.Handshake handshake -> "Successfully connected to server";
            case ServerOutboundMessage.IncompatibleVersions incompatibleVersions -> "Server not compatible";
            case ServerOutboundMessage.InvalidLogin invalidLogin -> "Invalid Login";
            case ServerOutboundMessage.InvalidSessionToken invalidSessionToken -> "Invalid session token";
            case ServerOutboundMessage.MessageSent messageSent -> "Message sent";
            case ServerOutboundMessage.RecentMessages recentMessages -> {
                String output = "";
                for (var msg : recentMessages.messages()) {
                    output += String.format("From %s to %s: %s", msg.fromAccountID(), msg.toAccountID(), msg.content());
                }
                yield output;
            }
            case ServerOutboundMessage.ServerError serverError -> "Server Error";
            case ServerOutboundMessage.SuccessfulLogin successfulLogin -> "Login successful";
        };
    }
}