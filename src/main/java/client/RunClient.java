package client;

import shared.*;

public class RunClient {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: RunClient <host> <port>");
        }

        final var config = new RunClient.ClientConfig(args[0], Integer.parseInt(args[1]));
        new RunClient().runClient(config);
    }

    public record ClientConfig(String host, int port) {}

    public void runClient(ClientConfig config) throws Exception {
        final ServerInterface server = new ServerInterfaceImpl(config.host, config.port);

        final ClientOutboundMessageParser clientOutboundMessageParser = new ClientOutboundMessageParserImpl();
        final ServerOutboundMessageParser serverOutboundMessageParser = new ServerOutboundMessageParserImpl();
        final ServerResponseFormatter fmter = new ServerResponseFormatterImpl();

        final ConsoleCommandParser parser = new ConsoleCommandParserImpl();

        final ConsoleCommandHandlerImpl.SendMessageToServer msgHandler = msg -> {
            try {
                return serverOutboundMessageParser.decodeMessage(server.sendServerCommand(clientOutboundMessageParser.encodeMessage(msg)));
            } catch (Exception e) {
                return new ServerOutboundMessage.ServerError("Could not message server");
            }
        };
        final ConsoleCommandHandler commandHandler = new ConsoleCommandHandlerImpl(msgHandler, parser, fmter);

        final ConsoleReader consoleReader = new ConsoleReaderImpl();
        consoleReader.listenForConsole(commandHandler);
    }

}
