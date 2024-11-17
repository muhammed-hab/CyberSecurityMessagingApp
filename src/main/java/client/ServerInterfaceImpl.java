package client;

import shared.ByteParsers;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ServerInterfaceImpl implements ServerInterface {

    private final String host;
    private final int port;

    public ServerInterfaceImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public byte[] sendServerCommand(byte[] message) throws Exception {
        SSLContext trustAll = SSLContext.getInstance("TLS");
        // Trust all certificates for learning purposes
        trustAll.init(null, new TrustManager[] {new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }}, null);
        SSLSocketFactory factory = trustAll.getSocketFactory();
        var socket = factory.createSocket(host, port);
//        var socket = new Socket(host, port);
        socket.getOutputStream().write(ByteParsers.intToBytes(message.length));
        socket.getOutputStream().write(message);
        socket.getOutputStream().flush();
        var respLen = ByteParsers.bytesToInt(socket.getInputStream().readNBytes(4));
        var resp = socket.getInputStream().readNBytes(respLen);
        socket.close();
        return resp;
    }
}
