package shared;

/**
 * A class that encodes and decodes {@link ServerOutboundMessage} to a binary format.
 * <pre>
 * All strings are encoded with UTF-8.
 * Messages are encoded as follows:
 * {@link shared.ServerOutboundMessage.Handshake}:
 *      4 bytes - message id = 1
 *      4 bytes - version
 * {@link shared.ServerOutboundMessage.IncompatibleVersions}:
 *      4 bytes - message id = 2
 *      4 bytes - version
 * {@link shared.ServerOutboundMessage.ServerError}:
 *      4 bytes - message id = 3
 *      4 bytes - error length
 *      x bytes - error
 * {@link shared.ServerOutboundMessage.RecentMessages}:
 *      4 bytes - message id = 4
 *
 *      Message format:
 *      4 bytes - From length
 *      x bytes - From
 *      4 bytes - To length
 *      x bytes - To
 *      4 bytes - Message length
 *      x bytes - Message
 *      4 bytes - Time
 *
 *      1 byte - is there another message: 0 if not, 1 if yes
 * {@link shared.ServerOutboundMessage.ConversationList}:
 *      4 bytes - message id = 5
 *
 *      With user format:
 *      4 bytes - username length
 *      x bytes - username
 *      1 byte - is there another user: 0 if not, 1 if yes
 * {@link shared.ServerOutboundMessage.InvalidLogin}
 *      4 bytes - message id = 6
 * {@link shared.ServerOutboundMessage.InvalidSessionToken}
 *      4 bytes - message id = 7
 * {@link shared.ServerOutboundMessage.SuccessfulLogin}
 *      4 bytes - message id = 8
 *      4 bytes - session token length
 *      x bytes - session token
 * {@link shared.ServerOutboundMessage.AccountCreated}
 *      4 bytes - message id = 9
 *      4 bytes - session token length
 *      x bytes - session token
 * {@link shared.ServerOutboundMessage.AccountAlreadyExists}
 *      4 bytes - message id = 10
 * {@link shared.ServerOutboundMessage.MessageSent}
 *      4 bytes - message id = 11
 * </pre>
 */
public interface ServerOutboundMessageParser {

    class MalformedMessageException extends Exception {}

    /**
     * Encodes the specified message into a byte format.
     * @param message The message to encode.
     * @return The bytes of the encoded message.
     */
    byte[] encodeMessage(ServerOutboundMessage message);

    /**
     * Decodes a message from a byte format.
     * @param message The message to decode.
     * @return The decoded message
     * @throws MalformedMessageException If the message does not follow the proper format.
     */
    ServerOutboundMessage decodeMessage(byte[] message) throws MalformedMessageException;

}
