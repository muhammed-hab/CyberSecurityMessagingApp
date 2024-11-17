package server;

import shared.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RunServer {
    public record ServerConfig(String dbURL, String certURL, String certPassword, String host, int port) {}

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            System.err.println("Usage: RunServer <database> <cert> <cert password> <host> <port>");
            System.exit(1);
        }

        final var config = new ServerConfig(args[0], args[1], args[2], args[3], Integer.parseInt(args[4]));

        new RunServer().startServer(config);
    }

    public void startServer(ServerConfig config) throws Exception {
        final PasswordHashes passwordHashes = new PasswordHashesImpl();
        final TimeFetcher timeFetcher = new SystemTimeFetcher();
        final ServerAccountHandler acctMgr;
        if (!Files.exists(Path.of(config.dbURL))) {
            var setup = Files.readString(Paths.get("setup.sql")).split(";");
            acctMgr = new SQLServerAccountHandler(
                    config.dbURL(),
                    passwordHashes,
                    timeFetcher,
                    setup
            );
        } else {
            acctMgr = new SQLServerAccountHandler(
                    config.dbURL,
                    passwordHashes,
                    timeFetcher
            );
        }
        final CertificateLoader certificateLoader = new CertificateLoaderImpl(config.certPassword);
        final var sslStore = certificateLoader.loadCertificate(new File(config.certURL));

        final ClientOutboundMessageParser clientOutboundMessageParser = new ClientOutboundMessageParserImpl();
        final ServerOutboundMessageParser serverOutboundMessageParser = new ServerOutboundMessageParserImpl();

        final ServerMessageHandler onMsgReceived = new ServerMessageHandlerImpl(
                serverOutboundMessageParser,
                clientOutboundMessageParser,
                acctMgr,
                timeFetcher
        );

        final SecureServer server = new SecureServerImpl();
        server.startServer(config.host, config.port, sslStore, onMsgReceived);
    }
}
