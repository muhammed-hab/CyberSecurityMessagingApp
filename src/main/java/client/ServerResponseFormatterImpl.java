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
            case ServerOutboundMessage.Handshake handshake -> null;
            case ServerOutboundMessage.IncompatibleVersions incompatibleVersions -> null;
            case ServerOutboundMessage.InvalidLogin invalidLogin -> null;
            case ServerOutboundMessage.InvalidSessionToken invalidSessionToken -> null;
            case ServerOutboundMessage.MessageSent messageSent -> null;
            case ServerOutboundMessage.RecentMessages recentMessages -> null;
            case ServerOutboundMessage.ServerError serverError -> null;
            case ServerOutboundMessage.SuccessfulLogin successfulLogin -> null;
        };
    }
}
