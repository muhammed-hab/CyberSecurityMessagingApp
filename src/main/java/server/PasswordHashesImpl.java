package server;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHashesImpl implements PasswordHashes {
    @Override
    public byte[] secureRandom(int size) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[size];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    @Override
    public byte[] saltPassword(byte[] password, byte[] salt) {
        byte[] saltedPassword = new byte[password.length];
        for (int i = 0; i < password.length; i++) {
            saltedPassword[i] = (byte) (password[i] ^ salt [i % salt.length]);
        }
        return saltedPassword;
    }

    @Override
    public byte[] hash(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }

    @Override
    public String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    @Override
    public byte[] decodeBase64(String data) {
        return Base64.getDecoder().decode(data);
    }
}
