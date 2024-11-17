package shared;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class ClientOutboundMessageParserImpl implements ClientOutboundMessageParser {

    public static final int SUPPORTED_VERSION = 1;

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
        output.writeBytes(ByteParsers.intToBytes(SUPPORTED_VERSION));
        output.writeBytes(ByteParsers.intToBytes(msgCode));

        output.writeBytes(ByteParsers.stringsToBytes(strings));

        return output.toByteArray();
    }

    @Override
    public ClientOutboundMessage decodeMessage(byte[] message) throws MalformedMessageException, IncompatibleVersionException {
        if (message.length < 8 || ByteParsers.getIntAt(message, 0) != SUPPORTED_VERSION) throw new IncompatibleVersionException();

        try {
            switch (ByteParsers.getIntAt(message, 4)) {
                case 1 -> {
                    return new ClientOutboundMessage.Handshake(SUPPORTED_VERSION);
                }
                case 2 -> {
                    var strs = ByteParsers.extractStrings(message, 8, 2);
                    return new ClientOutboundMessage.LogIn(strs[0], strs[1]);
                }
                case 3 -> {
                    var strs = ByteParsers.extractStrings(message, 8, 2);
                    return new ClientOutboundMessage.GetMessages(strs[0], strs[1]);
                }
                case 4 -> {
                    var strs = ByteParsers.extractStrings(message, 8, 1);
                    return new ClientOutboundMessage.GetConversations(strs[0]);
                }
                case 5 -> {
                    var strs = ByteParsers.extractStrings(message, 8, 3);
                    return new ClientOutboundMessage.SendMessage(strs[0], strs[1], strs[2]);
                }
                case 6 -> {
                    var strs = ByteParsers.extractStrings(message, 8, 2);
                    return new ClientOutboundMessage.CreateAccount(strs[0], strs[1]);
                }
                default -> throw new MalformedMessageException();
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedMessageException();
        }
    }
}
