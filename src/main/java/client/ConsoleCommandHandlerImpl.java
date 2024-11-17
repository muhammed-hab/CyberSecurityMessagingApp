package client;

import shared.ClientOutboundMessage;
import shared.ServerOutboundMessage;

public class ConsoleCommandHandlerImpl implements ConsoleCommandHandler {

    private final SendMessageToServer messenger;
    private final ConsoleCommandParser parser;
    private final ServerResponseFormatter fmter;

    public interface SendMessageToServer {
        ServerOutboundMessage sendMessageToServer(ClientOutboundMessage msg);
    }

    public ConsoleCommandHandlerImpl(
            SendMessageToServer messenger,
            ConsoleCommandParser parser,
            ServerResponseFormatter fmter) {
        this.messenger = messenger;
        this.parser = parser;
        this.fmter = fmter;
    }

    private String token;

    @Override
    public String handleConsoleCommand(String input) {
        try {
            var response = switch (parser.parseCommand(input)) {
                case ConsoleCommand.CheckConnection checkConnection -> messenger.sendMessageToServer(new ClientOutboundMessage.Handshake(1L));
                case ConsoleCommand.CreateAccount createAccount -> {
                    var resp = messenger.sendMessageToServer(new ClientOutboundMessage.CreateAccount(createAccount.username(), createAccount.password()));
                    if (resp instanceof ServerOutboundMessage.AccountCreated(String sessionToken)) {
                        token = sessionToken;
                    }
                    yield resp;
                }
                case ConsoleCommand.GetConversations getConversations -> {
                    if (token == null) yield new ServerOutboundMessage.InvalidLogin();
                    yield messenger.sendMessageToServer(new ClientOutboundMessage.GetConversations(token));
                }
                case ConsoleCommand.GetMessagesWith getMessagesWith -> {
                    if (token == null) yield new ServerOutboundMessage.InvalidLogin();
                    yield messenger.sendMessageToServer(new ClientOutboundMessage.GetMessages(token, getMessagesWith.username()));
                }
                case ConsoleCommand.LogIn logIn -> {
                    var resp = messenger.sendMessageToServer(new ClientOutboundMessage.LogIn(logIn.username(), logIn.password()));
                    if (resp instanceof ServerOutboundMessage.SuccessfulLogin(String sessionToken)) {
                        token = sessionToken;
                    }
                    yield resp;
                }
                case ConsoleCommand.SendMessage sendMessage -> {
                    if (token == null) yield new ServerOutboundMessage.InvalidLogin();
                    yield messenger.sendMessageToServer(new ClientOutboundMessage.SendMessage(token, sendMessage.toUser(), sendMessage.message()));
                }
            };
            return fmter.formatResponse(response);
        } catch(ConsoleCommandParser.InvalidCommandError err) {
            return "Invalid command";
        }
    }
}
