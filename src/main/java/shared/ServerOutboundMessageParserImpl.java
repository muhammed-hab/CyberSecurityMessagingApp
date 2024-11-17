package shared;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ServerOutboundMessageParserImpl implements ServerOutboundMessageParser {
    public static final int SUPPORTED_VERSION = 1;

    @Override
    public byte[] encodeMessage(ServerOutboundMessage message) {
        int messageId;
        String[] strings = new String[0];
        byte[] extraData = new byte[0];

        switch (message) {
            case ServerOutboundMessage.Handshake handshake -> {
                messageId = 1;
                extraData = ByteParsers.intToBytes(SUPPORTED_VERSION);
            }
            case ServerOutboundMessage.IncompatibleVersions incompatibleVersions -> messageId = 2;
            case ServerOutboundMessage.ServerError serverError -> {
                messageId = 3;
                strings = new String[] {serverError.error()};
            }
            case ServerOutboundMessage.RecentMessages recentMessages -> {
                var messages = recentMessages.messages();
                messageId = 4;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                for (Message value : messages) {
                    bytes.write(1);

                    bytes.writeBytes(ByteParsers.stringsToBytes(
                            new String[]{value.fromAccountID(), value.toAccountID(), value.content()}
                    ));
                    bytes.writeBytes(ByteParsers.intToBytes((int) value.time()));
                }
                bytes.write(0);
                extraData = bytes.toByteArray();
            }
            case ServerOutboundMessage.ConversationList conversationList -> {
                var with = conversationList.with();
                messageId = 5;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                for (String s : with) {
                    bytes.write(1);

                    bytes.writeBytes(ByteParsers.stringsToBytes(
                            new String[]{s}
                    ));
                }
                bytes.write(0);
                extraData = bytes.toByteArray();
            }
            case ServerOutboundMessage.InvalidLogin invalidLogin -> messageId = 6;
            case ServerOutboundMessage.InvalidSessionToken invalidSessionToken -> messageId = 7;
            case ServerOutboundMessage.SuccessfulLogin successfulLogin -> {
                messageId = 8;
                strings = new String[] {successfulLogin.sessionToken()};
            }
            case ServerOutboundMessage.AccountCreated accountCreated -> {
                messageId = 9;
                strings = new String[] {accountCreated.sessionToken()};
            }
            case ServerOutboundMessage.AccountAlreadyExists accountAlreadyExists -> messageId = 10;
            case ServerOutboundMessage.MessageSent messageSent -> messageId = 11;
        }

        var strBytes = ByteParsers.stringsToBytes(strings);
        var output = new byte[4 + strBytes.length + extraData.length];
        System.arraycopy(ByteParsers.intToBytes(messageId), 0, output, 0, 4);
        System.arraycopy(strBytes, 0, output, 4, strBytes.length);
        System.arraycopy(extraData, 0, output, 4 + strBytes.length, extraData.length);
        return output;
    }

    @Override
    public ServerOutboundMessage decodeMessage(byte[] message) throws MalformedMessageException {
        try {
            return switch (ByteParsers.getIntAt(message, 0)) {
                case 1 -> new ServerOutboundMessage.Handshake(ByteParsers.getIntAt(message, 0));
                case 2 -> new ServerOutboundMessage.IncompatibleVersions();
                case 3 -> new ServerOutboundMessage.ServerError(ByteParsers.extractStrings(message, 4, 1)[0]);
                case 4 -> {
                    var msges = new ArrayList<Message>();
                    var loc = 4;
                    while (message[loc] == 1) {
                        loc += 1;

                        var res = ByteParsers.extractStringsWithCount(message, loc, 3);
                        var timestamp = ByteParsers.getIntAt(message, loc + res.bytesRead());
                        msges.add(new Message(res.strings()[0], res.strings()[1], res.strings()[2], timestamp));

                        loc += res.bytesRead() + 4;
                    }
                    yield new ServerOutboundMessage.RecentMessages(msges.toArray(new Message[0]));
                }
                case 5 -> {
                    var with =  new ArrayList<String>();
                    var loc = 4;
                    while (message[loc] == 1) {
                        loc += 1;

                        var res = ByteParsers.extractStringsWithCount(message, loc, 1);
                        with.add(res.strings()[0]);

                        loc += res.bytesRead();
                    }
                    yield new ServerOutboundMessage.ConversationList(with.toArray(new String[0]));
                }
                case 6 -> new ServerOutboundMessage.InvalidLogin();
                case 7 -> new ServerOutboundMessage.InvalidSessionToken();
                case 8 -> new ServerOutboundMessage.SuccessfulLogin(ByteParsers.extractStrings(message, 4, 1)[0]);
                case 9 -> new ServerOutboundMessage.AccountCreated(ByteParsers.extractStrings(message, 4, 1)[0]);
                case 10 -> new ServerOutboundMessage.AccountAlreadyExists();
                case 11 -> new ServerOutboundMessage.MessageSent();
                default -> throw new MalformedMessageException();
            };
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedMessageException();
        }
    }
}
