package server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shared.Message;
import shared.SystemTimeFetcher;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SQLServerAccountHandlerTest {

    static class TestPasswordHashes implements PasswordHashes {

        @Override
        public byte[] secureRandom(int size) throws Exception {
            return new byte[size];
        }

        @Override
        public byte[] saltPassword(byte[] password, byte[] salt) {
            return new byte[password.length];
        }

        @Override
        public byte[] hash(byte[] data) throws Exception {
            return new byte[32];
        }

        @Override
        public String encodeBase64(byte[] data) {
            var output = new byte[Math.ceilDiv(data.length * 6, 8)];
            Arrays.fill(output, (byte) 'a');
            return new String(output);
        }

        @Override
        public byte[] decodeBase64(String data) {
            var output = new byte[Math.ceilDiv(data.length() * 8, 6)];
            Arrays.fill(output, (byte) 'a');
            return output;
        }
    }

    private ServerAccountHandler handler;
    @BeforeEach
    void setUp() throws Exception {
        var setup = Files.readString(Paths.get("setup.sql")).split(";");
        handler = new SQLServerAccountHandler(":memory:", new TestPasswordHashes(), new SystemTimeFetcher(), setup);
    }

    @Test
    void authentication() throws Exception {
        assertThrows(ServerAccountHandler.AccountDoesNotExistException.class,
                () -> handler.getSessionToken("username", "password"));
        handler.createAccount("username", "password");
        assertEquals(
                handler.getAccountID(
                        handler.getSessionToken("username", "password")
                ),
                "username");
        assertThrows(ServerAccountHandler.AccountAlreadyExistsException.class,
                () -> handler.createAccount("username", "password"));
    }

    @Test
    void testMessages() throws Exception {
        handler.createAccount("user1", "");

        assertEquals(handler.getConversations("user1").length, 0);
        assertThrows(ServerAccountHandler.AccountDoesNotExistException.class,
                () -> handler.saveMessage(new Message("user1", "user2", "asd", 123)));
        assertThrows(ServerAccountHandler.AccountDoesNotExistException.class,
                () -> handler.saveMessage(new Message("user2", "user1", "asd", 123)));

        handler.createAccount("user2", "");

        var messages = new Message[] {
                new Message("user1", "user2", "asd", 354),
                new Message("user2", "user1", "response to asd", 124),
                new Message("user1", "user2", "msg1", 12),
        };

        for (var message : messages) handler.saveMessage(message);

        assertArrayEquals(handler.getMessages("user1", "user2"), messages);
        assertArrayEquals(handler.getMessages("user2", "user1"), messages);

        handler.createAccount("user3", "");
        handler.saveMessage(new Message("user1", "user3", "msg1", 345));
        assertArrayEquals(handler.getConversations("user1"), new String[] {"user2", "user3"});
        assertArrayEquals(handler.getConversations("user2"), new String[] {"user1"});
        assertArrayEquals(handler.getConversations("user3"), new String[] {"user1"});
    }

    @Test
    void insertLog() throws Exception {
        var logs = new ServerAccountHandler.Log[] {
                new ServerAccountHandler.Log("asd", 53),
                new ServerAccountHandler.Log("afg", 50)
        };
        for (var log : logs) handler.insertLog(log);
        assertArrayEquals(logs, handler.getLogs());
    }
}