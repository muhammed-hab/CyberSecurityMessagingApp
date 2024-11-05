package shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerOutboundMessageParserTest {

    ServerOutboundMessageParser createCoder() {
        throw new RuntimeException("Not implemented!");
    }

    @Test
    void encodeAndDecodeMessage() throws ServerOutboundMessageParser.MalformedMessageException {
        var coder = createCoder();

        var messages = new ServerOutboundMessage[] {
                new ServerOutboundMessage.Handshake(1L),
                new ServerOutboundMessage.IncompatibleVersions(),
                new ServerOutboundMessage.ServerError("Test error"),
                new ServerOutboundMessage.RecentMessages(new Message[] {
                        new Message("from", "to", "msg", 123),
                        new Message("to", "from", "msg2", 456),
                }),
                new ServerOutboundMessage.ConversationList(new String[] {"User1", "User2"}),
                new ServerOutboundMessage.InvalidLogin(),
                new ServerOutboundMessage.InvalidSessionToken(),
                new ServerOutboundMessage.SuccessfulLogin("token"),
                new ServerOutboundMessage.AccountCreated("token"),
                new ServerOutboundMessage.AccountAlreadyExists(),
                new ServerOutboundMessage.MessageSent()
        };

        for (var message : messages) {
            assertEquals(message, coder.decodeMessage(coder.encodeMessage(message)));
        }

    }

}