package server;

import shared.TimeFetcher;

public class PassthroughLogger implements ServerMessageHandler {

    private final ServerAccountHandler accountHandler;
    private final ServerMessageHandler messageHandler;
    private final PasswordHashes hashes;
    private final TimeFetcher time;

    public PassthroughLogger(ServerAccountHandler accountHandler, ServerMessageHandler messageHandler, PasswordHashes hashes, TimeFetcher time) {
        this.accountHandler = accountHandler;
        this.messageHandler = messageHandler;
        this.hashes = hashes;
        this.time = time;
    }

    @Override
    public byte[] handleMessage(byte[] message) {
        try {
            accountHandler.insertLog(new ServerAccountHandler.Log(hashes.encodeBase64(message), (int) time.getCurrentTimeMillis()));
        } catch (Exception e) {
            System.err.printf("Failed to insert a log: %s\nLog: %d @ %s \n",
                    e.getMessage(),
                    time.getCurrentTimeMillis(),
                    hashes.encodeBase64(message)
            );
        }
        return messageHandler.handleMessage(message);
    }
}
