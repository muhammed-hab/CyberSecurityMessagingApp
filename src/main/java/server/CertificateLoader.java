package server;

import javax.net.ssl.SSLContext;
import java.io.File;

public interface CertificateLoader {

    /**
     * Loads the stored RSA private key certificate.
     * @param file Certificate location.
     * @return SSLContext including the certificate.
     * @throws Exception If there are errors loading the certificate
     */
    SSLContext loadCertificate(File file) throws Exception;

}
