package server;

import shared.*;

public class ServerMessageHandlerImpl implements ServerMessageHandler {

    private final ServerOutboundMessageParser serverOutboundMessageParser;
    private final ClientOutboundMessageParser clientOutboundMessageParser;
    private final ServerAccountHandler serverAccountHandler;
    private final TimeFetcher timeFetcher;

    public ServerMessageHandlerImpl(ServerOutboundMessageParser serverOutboundMessageParser,
                                ClientOutboundMessageParser clientOutboundMessageParser,
                                ServerAccountHandler serverAccountHandler,
                                    TimeFetcher timeFetcher) {
        this.serverOutboundMessageParser = serverOutboundMessageParser;
        this.clientOutboundMessageParser = clientOutboundMessageParser;
        this.serverAccountHandler = serverAccountHandler;
        this.timeFetcher = timeFetcher;
    }

    @Override
    public byte[] handleMessage(byte[] message) {
        ServerOutboundMessage output;
        try {
            var clientMsg = clientOutboundMessageParser.decodeMessage(message);

            output = switch (clientMsg) {
                case ClientOutboundMessage.GetMessages getMsg -> {
                    try {
                        var messages = serverAccountHandler.getMessages(
                                serverAccountHandler.getAccountID(getMsg.sessionToken()),
                                getMsg.userId()
                        );
                        yield new ServerOutboundMessage.RecentMessages(messages);
                    } catch (ServerAccountHandler.TokenExpiredException |
                             ServerAccountHandler.InvalidTokenException e) {
                        yield new ServerOutboundMessage.InvalidSessionToken();
                    } catch (Exception e) {
                        yield new ServerOutboundMessage.ServerError(e.getMessage());
                    }
                }
                case ClientOutboundMessage.CreateAccount createAccount -> {
                    try {
                        serverAccountHandler.createAccount(createAccount.username(), createAccount.password());
                        yield new ServerOutboundMessage.SuccessfulLogin(
                                serverAccountHandler.getSessionToken(createAccount.username(), createAccount.password())
                        );
                    } catch (ServerAccountHandler.AccountAlreadyExistsException e) {
                        yield new ServerOutboundMessage.AccountAlreadyExists();
                    } catch (ServerAccountHandler.AccountDoesNotExistException |
                             ServerAccountHandler.InvalidLoginException e) {
                        yield new ServerOutboundMessage.ServerError("Could not get session token, but account was created.");
                    } catch (Exception e) {
                        yield new ServerOutboundMessage.ServerError(e.getMessage());
                    }
                }
                case ClientOutboundMessage.GetConversations getConversations -> {
                    try {
                        var with = serverAccountHandler.getConversations(
                                serverAccountHandler.getAccountID(getConversations.sessionToken())
                        );
                        yield new ServerOutboundMessage.ConversationList(with);
                    } catch (ServerAccountHandler.TokenExpiredException |
                             ServerAccountHandler.InvalidTokenException e) {
                        yield new ServerOutboundMessage.InvalidSessionToken();
                    } catch (Exception e) {
                        yield new ServerOutboundMessage.ServerError(e.getMessage());
                    }
                }
                case ClientOutboundMessage.Handshake handshake -> new ServerOutboundMessage.Handshake(handshake.version());
                case ClientOutboundMessage.LogIn logIn -> {
                    try {
                        yield new ServerOutboundMessage.SuccessfulLogin(
                                serverAccountHandler.getSessionToken(logIn.username(), logIn.password())
                        );
                    } catch (ServerAccountHandler.InvalidLoginException |
                             ServerAccountHandler.AccountDoesNotExistException e) {
                        yield new ServerOutboundMessage.InvalidLogin();
                    } catch (Exception e) {
                        yield new ServerOutboundMessage.ServerError(e.getMessage());
                    }
                }
                case ClientOutboundMessage.SendMessage sendMessage -> {
                    try {
                        serverAccountHandler.saveMessage(
                                new Message(
                                        serverAccountHandler.getAccountID(sendMessage.sessionToken()),
                                        sendMessage.to(),
                                        sendMessage.message(),
                                        timeFetcher.getCurrentTimeMillis()
                                        )
                        );
                        yield new ServerOutboundMessage.MessageSent();
                    } catch (ServerAccountHandler.TokenExpiredException |
                             ServerAccountHandler.InvalidTokenException e) {
                        yield new ServerOutboundMessage.InvalidSessionToken();
                    } catch (Exception e) {
                        yield new ServerOutboundMessage.ServerError(e.getMessage());
                    }
                }
            };
        } catch (ClientOutboundMessageParser.MalformedMessageException e) {
            output = new ServerOutboundMessage.ServerError("Could not parse message");
        } catch (ClientOutboundMessageParser.IncompatibleVersionException e) {
            output = new ServerOutboundMessage.IncompatibleVersions();
        }
        return serverOutboundMessageParser.encodeMessage(output);
    }
}
