package server;

public class PasswordHashesImpl implements PasswordHashes {
    @Override
    public byte[] secureRandom(int size) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public byte[] saltPassword(byte[] password, byte[] salt) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public byte[] hash(byte[] data) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String encodeBase64(byte[] data) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public byte[] decodeBase64(String data) {
        throw new RuntimeException("Not implemented");
    }
}
