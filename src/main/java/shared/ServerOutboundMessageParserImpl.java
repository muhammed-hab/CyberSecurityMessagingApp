package shared;

public class ServerOutboundMessageParserImpl implements ServerOutboundMessageParser {
    @Override
    public byte[] encodeMessage(ServerOutboundMessage message) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ServerOutboundMessage decodeMessage(byte[] message) throws MalformedMessageException {
        throw new RuntimeException("Not implemented");
    }
}
