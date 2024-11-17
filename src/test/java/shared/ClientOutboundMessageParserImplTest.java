package shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientOutboundMessageParserImplTest {

    @Test
    void encodeAndDecodeMessage() throws ClientOutboundMessageParser.IncompatibleVersionException, ClientOutboundMessageParser.MalformedMessageException {

        var coder = new ClientOutboundMessageParserImpl();

        var messages = new ClientOutboundMessage[] {
             new ClientOutboundMessage.CreateAccount("username", "password"),
             new ClientOutboundMessage.GetMessages("session token", "other user"),
             new ClientOutboundMessage.GetConversations("session token"),
             new ClientOutboundMessage.Handshake(1L),
             new ClientOutboundMessage.LogIn("username", "password"),
             new ClientOutboundMessage.SendMessage("session token", "to", "message")
        };

        for (var message : messages) {
            assertEquals(message, coder.decodeMessage(coder.encodeMessage(message)));
        }

    }

    @Test
    void testBitEncoding() {
        var nums = new int[] {0x34, 0xFF, 0xFFFF, 0x3413, 0xFFFFFF, 0x342343, 0xFFFFFFFF, 0xFF239012};

        for (var num : nums) {
            assertEquals(num, ByteParsers.bytesToInt(ByteParsers.intToBytes(num)));
        }
    }
}