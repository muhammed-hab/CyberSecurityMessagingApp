package shared;

/**
 * A class that encodes and decodes {@link ClientOutboundMessage} to a binary format.
 * <pre>
 * All strings are encoded with UTF-8.
 * Messages are encoded as follows:
 * {@link shared.ClientOutboundMessage.Handshake}:
 *      4 bytes - version
 *      4 bytes - message type = 1
 * {@link shared.ClientOutboundMessage.LogIn}:
 *      4 bytes - version
 *      4 bytes - message type = 2
 *      4 bytes - Username length
 *      x bytes - Username
 *      4 bytes - Password length
 *      x bytes - Password
 * {@link shared.ClientOutboundMessage.GetMessages}:
 *      4 bytes - version
 *      4 bytes - message type = 3
 *      4 bytes - Session token length
 *      x bytes - Session token
 *      4 bytes - Other UserID length
 *      x bytes - Other UserID
 * {@link shared.ClientOutboundMessage.GetConversations}:
 *      4 bytes - version
 *      4 bytes - message type = 4
 *      4 bytes - Session token length
 *      x bytes - Session token
 * {@link shared.ClientOutboundMessage.SendMessage}:
 *      4 bytes - version
 *      4 bytes - message type = 5
 *      4 bytes - Session token length
 *      x bytes - Session token
 *      4 bytes - To UserID length
 *      x bytes - To UserID
 *      4 bytes - Message length
 *      x bytes - Message
 * {@link shared.ClientOutboundMessage.CreateAccount}:
 *      4 bytes - version
 *      4 bytes - message type = 6
 *      4 bytes - Username Length
 *      x bytes - Username
 *      4 bytes - Password Length
 *      x bytes - Password
 * </pre>
 */
public interface ClientOutboundMessageParser {

    class MalformedMessageException extends Exception {}
    class IncompatibleVersionException extends Exception {}

    /**
     * Encodes the specified message into a byte format.
     * @param message The message to encode.
     * @return The bytes of the encoded message.
     */
    byte[] encodeMessage(ClientOutboundMessage message);

    /**
     * Decodes a message from a byte format.
     * @param message The message to decode.
     * @return The decoded message
     * @throws MalformedMessageException If the message does not follow the proper format.
     * @throws IncompatibleVersionException If the message specifies a different version than this server.
     */
    ClientOutboundMessage decodeMessage(byte[] message) throws MalformedMessageException, IncompatibleVersionException;

}
