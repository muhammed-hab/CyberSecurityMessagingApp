package server;

public interface PasswordHashes {

    /**
     * Generates secure random bytes
     * @param size The number of random bytes needed
     * @return A byte array of random data.
     * @throws Exception If there was an error generating the data
     */
    byte[] secureRandom(int size) throws Exception;

    /**
     * Applies the salt to the password through XOR, repeating as necessary.
     * @param password The password to salt.
     * @param salt The salt to apply
     * @return The salted password
     */
    byte[] saltPassword(byte[] password, byte[] salt);

    /**
     * Generates an SHA-256 hash of the provided data.
     * @param data The provided data.
     * @return The hash.
     * @throws Exception If there was an error hashing.
     */
    byte[] hash(byte[] data) throws Exception;

    /**
     * Encodes the provided data with Base64.
     * @param data The data to encode.
     * @return The encoded data in Base64.
     */
    String encodeBase64(byte[] data);

    /**
     * Decodes the provided data with Base64.
     * @param data The Base64 data.
     * @return The original bytes.
     */
    byte[] decodeBase64(String data);

}
