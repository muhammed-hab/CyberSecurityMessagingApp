package shared;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class ClientOutboundMessageParserImpl implements ClientOutboundMessageParser {

    public static final int SUPPORTED_VERSION = 1;

    public static int bytesToInt(byte[] data) {
        if (data.length != 4) throw new RuntimeException("Int byte array must be of length 4!");
        return (((int) data[0]) & 0xFF) | ((((int) data[1]) & 0xFF) << 8) | ((((int) data[2]) & 0xFF) << 16) | ((((int) data[3]) & 0xFF) << 24);
    }

    public static byte[] intToBytes(int num) {
        return new byte[] {
                (byte) (num & 0xFF),
                (byte) ((num >>> 8) & 0xFF),
                (byte) ((num >>> 16) & 0xFF),
                (byte) ((num >>> 24) & 0xFF),
        };
    }

    @Override
    public byte[] encodeMessage(ClientOutboundMessage message) throws IncompatibleVersionException {
        final int msgCode;
        final String[] strings;
        switch (message) {
            case ClientOutboundMessage.Handshake handshake -> {
                msgCode = 1;
                if (handshake.version() != SUPPORTED_VERSION) throw new IncompatibleVersionException();
                strings = new String[]{};
            }
            case ClientOutboundMessage.LogIn logIn -> {
                msgCode = 2;
                strings = new String[]{logIn.username(), logIn.password()};
            }
            case ClientOutboundMessage.GetMessages getMessages -> {
                msgCode = 3;
                strings = new String[]{getMessages.sessionToken(), getMessages.userId()};
            }
            case ClientOutboundMessage.GetConversations getConversations -> {
                msgCode = 4;
                strings = new String[]{getConversations.sessionToken()};
            }
            case ClientOutboundMessage.SendMessage msg -> {
                msgCode = 5;
                strings = new String[]{msg.sessionToken(), msg.to(), msg.message()};
            }
            case ClientOutboundMessage.CreateAccount createAccount -> {
                msgCode = 6;
                strings = new String[]{createAccount.username(), createAccount.password()};
            }
        }

        var output = new ByteArrayOutputStream();
        output.writeBytes(intToBytes(SUPPORTED_VERSION));
        output.writeBytes(intToBytes(msgCode));

        for (var str : strings) {
            output.writeBytes(intToBytes(str.length()));
            output.writeBytes(str.getBytes(StandardCharsets.UTF_8));
        }

        return output.toByteArray();
    }

    public static int getIntAt(byte[] array, int offset) {
        byte[] output = new byte[4];
        System.arraycopy(array, offset, output, 0, 4);
        return bytesToInt(output);
    }

    String[] extractStrings(byte[] message, int numStrings) throws MalformedMessageException {
        var outputs = new String[numStrings];
        int loc = 8;
        for (int i = 0; i < numStrings; i++) {
            if (message.length < loc + 4) throw new MalformedMessageException();

            var strLen = getIntAt(message, loc);
            if (message.length < loc + 4 + strLen) throw new MalformedMessageException();
            outputs[i] = new String(message, loc + 4, strLen, StandardCharsets.UTF_8);

            loc = loc + 4 + strLen;
        }
        return outputs;
    }

    @Override
    public ClientOutboundMessage decodeMessage(byte[] message) throws MalformedMessageException, IncompatibleVersionException {
        if (message.length < 8 || getIntAt(message, 0) != SUPPORTED_VERSION) throw new IncompatibleVersionException();

        switch (getIntAt(message, 4)) {
            case 1 -> {
                return new ClientOutboundMessage.Handshake(SUPPORTED_VERSION);
            }
            case 2 -> {
                var strs = extractStrings(message, 2);
                return new ClientOutboundMessage.LogIn(strs[0], strs[1]);
            }
            case 3 -> {
                var strs = extractStrings(message, 2);
                return new ClientOutboundMessage.GetMessages(strs[0], strs[1]);
            }
            case 4 -> {
                var strs = extractStrings(message, 1);
                return new ClientOutboundMessage.GetConversations(strs[0]);
            }
            case 5 -> {
                var strs = extractStrings(message, 3);
                return new ClientOutboundMessage.SendMessage(strs[0], strs[1], strs[2]);
            }
            case 6 -> {
                var strs = extractStrings(message, 2);
                return new ClientOutboundMessage.CreateAccount(strs[0], strs[1]);
            }
            default -> throw new MalformedMessageException();
        }
    }
}
