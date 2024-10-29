package client;

import shared.ServerOutboundMessage;

/**
 * Messages should be formatted as follows:
 * <pre>
 * {@link ServerOutboundMessage.Handshake}
 *      Connected to server version [version]
 * {@link ServerOutboundMessage.IncompatibleVersions}
 *      The server is not compatible with this client. The server is on version [version]
 * etc.
 * </pre>
 */
public interface ServerResponseFormatter {

    /**
     * Converts a server response to a user-friendly display.
     * @param message The message to format.
     * @return A string representing the server's message
     */
    String formatResponse(ServerOutboundMessage message);

}
