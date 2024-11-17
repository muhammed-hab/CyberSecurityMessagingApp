package shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerOutboundMessageParserTest {

    ServerOutboundMessageParser createCoder() {
        return new ServerOutboundMessageParserImpl();
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
            System.out.println(message);
            var decoded = coder.decodeMessage(coder.encodeMessage(message));
            if (message instanceof ServerOutboundMessage.RecentMessages recent1
                    && decoded instanceof ServerOutboundMessage.RecentMessages recent2) {
                assertArrayEquals(recent1.messages(), recent2.messages());
            } else if (message instanceof ServerOutboundMessage.ConversationList list1
                    && decoded instanceof ServerOutboundMessage.ConversationList list2) {
                assertArrayEquals(list1.with(), list2.with());
            } else {
                assertEquals(message, decoded);
            }
        }

    }

}