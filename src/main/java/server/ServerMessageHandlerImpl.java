package server;

import shared.ClientOutboundMessageParser;
import shared.ServerOutboundMessageParser;

public class ServerMessageHandlerImpl implements ServerMessageHandler {

    private final ServerOutboundMessageParser serverOutboundMessageParser;
    private final ClientOutboundMessageParser clientOutboundMessageParser;
    private final ServerAccountHandler serverAccountHandler;

    public ServerMessageHandlerImpl(ServerOutboundMessageParser serverOutboundMessageParser,
                                ClientOutboundMessageParser clientOutboundMessageParser,
                                ServerAccountHandler serverAccountHandler) {
        this.serverOutboundMessageParser = serverOutboundMessageParser;
        this.clientOutboundMessageParser = clientOutboundMessageParser;
        this.serverAccountHandler = serverAccountHandler;
    }

    @Override
    public byte[] handleMessage(byte[] message) {
        throw new RuntimeException("Not implemented");
    }
}
