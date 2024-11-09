package server;

import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

public class CertificateLoaderImpl implements CertificateLoader {

    private static final String KEYSTORE_TYPE = "JKS";  // Java KeyStore (JKS) format
    private static final String KEY_MANAGER_ALGORITHM = "SunX509"; // Algorithm used by KeyManagerFactory
    private static final String TLS_PROTOCOL = "TLS";  // TLS protocol for SSLContext

    private final String keystorePassword;

    /**
     * Constructor for CertificateLoaderImpl.
     * @param keystorePassword The password for the keystore.
     */
    public CertificateLoaderImpl(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    /**
     * Loads the stored RSA private key certificate from a given file location.
     * @param file Certificate file in JKS format.
     * @return SSLContext initialized with the certificate.
     * @throws Exception If there are errors loading the certificate.
     */
    @Override
    public SSLContext loadCertificate(File file) throws Exception {
        // Load the KeyStore from the specified file
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        try (FileInputStream fis = new FileInputStream(file)) {
            keyStore.load(fis, keystorePassword.toCharArray());
        }

        // Initialize a KeyManagerFactory with the KeyStore
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KEY_MANAGER_ALGORITHM);
        keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

        // Initialize SSLContext with the KeyManager
        SSLContext sslContext = SSLContext.getInstance(TLS_PROTOCOL);
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        return sslContext;
    }
}
