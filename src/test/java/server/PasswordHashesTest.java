package server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashesTest {

    @BeforeEach
    void setupPasswordHasher() {
        hasher = new PasswordHashesImpl();
    }

    PasswordHashes hasher;

    @Test
    void secureRandom() throws Exception {
        for (int i = 1; i <= 16; i++) {
            var result = hasher.secureRandom(i);
            assertEquals(i, result.length);
            // verify that it is not array of zeros
            assertFalse(Arrays.equals(new byte[i], result));
        }
    }

    @Test
    void saltPassword() {
        var salt = new byte[] { 0x0, -0x1, 0x0, -0x1 };
        // all 1's or 0's for easy checking
        var password = "Test message".getBytes(StandardCharsets.UTF_8);
        var digest = hasher.saltPassword(password, salt);

        assertEquals(password.length, digest.length);
        for (int i = 0; i < digest.length; i++) {
            if (i % 2 == 0) {
                assertEquals(password[i], digest[i]);
            } else {
                assertEquals(~password[i], digest[i]);
            }
        }
    }

    String[] messages = new String[] {"First", "Second", "Third",
            "A really long message123456789012345678901234567890123456789012345678901234567890123456789012345" +
                    "678901234567890"};

    @Test
    void hash() throws Exception {
        for (var message : messages) {
            var hash1 = hasher.hash(message.getBytes(StandardCharsets.UTF_8));
            var hash2 = hasher.hash(message.getBytes(StandardCharsets.UTF_8));

            // SHA-256 32 bytes
            assertEquals(32, hash1.length);
            assertEquals(32, hash2.length);
            assertArrayEquals(hash1, hash2);
        }
    }

    @Test
    void encodeBase64() {
        var codings = new String[][] {
                new String[] {"Message 1", "TWVzc2FnZSAx"},
                new String[] {"Second Message", "U2Vjb25kIE1lc3NhZ2U="}
        };

        for (var msgPair : codings) {
            assertEquals(msgPair[1], hasher.encodeBase64(msgPair[0].getBytes(StandardCharsets.UTF_8)));
            assertArrayEquals(msgPair[0].getBytes(StandardCharsets.UTF_8), hasher.decodeBase64(msgPair[1]));
        }

        for (var msg: messages) {
            var bytes = msg.getBytes(StandardCharsets.UTF_8);
            assertArrayEquals(bytes, hasher.decodeBase64(hasher.encodeBase64(bytes)));
        }
    }
}