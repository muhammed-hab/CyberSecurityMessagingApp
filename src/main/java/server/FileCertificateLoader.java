package server;

import javax.net.ssl.SSLContext;
import java.io.File;

public class FileCertificateLoader implements CertificateLoader {
    @Override
    public SSLContext loadCertificate(File file) throws Exception {
        throw new RuntimeException("Not implemented");
    }
}
