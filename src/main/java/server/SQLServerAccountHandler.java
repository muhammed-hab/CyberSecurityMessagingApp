package server;

import shared.Message;
import shared.TimeFetcher;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLServerAccountHandler implements ServerAccountHandler {

    private final Connection conn;
    private final PasswordHashes hashes;
    private final TimeFetcher time;

    private final int SESSION_VALIDITY_MS = 1_000_000;

    public SQLServerAccountHandler(String connUrl, PasswordHashes hashes, TimeFetcher time) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + connUrl);
        this.hashes = hashes;
        this.time = time;
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
    }
    public SQLServerAccountHandler(String connUrl, PasswordHashes hashes, TimeFetcher time, String[] setupScript) throws SQLException {
        this(connUrl, hashes, time);
        var stmt = conn.createStatement();
        for (var str : setupScript) {
            stmt.addBatch(str);
        }
        stmt.executeBatch();
    }

    @Override
    public void createAccount(String username, String password) throws Exception {
        var stmt = conn.createStatement();
        var salt = hashes.secureRandom(8);
        var passwordHash = hashes.hash(hashes.saltPassword(password.getBytes(StandardCharsets.UTF_8), salt));

        // SQL Injection intentionally left in for learning purposes
        try {
            stmt.execute(String.format("INSERT INTO accounts (username, passwordHash, salt) VALUES ('%s', '%s', '%s');",
                    username, hashes.encodeBase64(passwordHash), hashes.encodeBase64(salt)));
        } catch (SQLException err) {
            // (1555) SQLITE_CONSTRAINT_PRIMARYKEY - likely due to not unique username
            // (19) SQLITE_CONSTRAINT
            if (err.getErrorCode() == 1555 || err.getErrorCode() == 19) throw new AccountAlreadyExistsException();
            else throw err;
        }
    }

    @Override
    public void saveMessage(Message message) throws Exception {
        var stmt = conn.createStatement();

        // SQL Injection intentionally left in for learning purposes
        try {
            stmt.execute(String.format("INSERT INTO messages(fromUser, toUser, message, msgTime) VALUES ('%s', '%s', '%s', '%s');",
                    message.fromAccountID(), message.toAccountID(), message.content(), message.time()));
        } catch (SQLException err) {
            // (787) SQLITE_CONSTRAINT_FOREIGNKEY - likely due to an account not existing
            // (19) SQLITE_CONSTRAINT
            if (err.getErrorCode() == 787 || err.getErrorCode() == 19) throw new AccountDoesNotExistException();
            else throw err;
        }
    }

    @Override
    public String[] getConversations(String userID) throws Exception {
        var stmt = conn.createStatement();

        // SQL Injection intentionally left in for learning purposes
        var result = stmt.executeQuery(String.format("SELECT DISTINCT toUser as user, NULL as userExists FROM messages WHERE fromUser='%s' UNION SELECT DISTINCT fromUser as user, NULL as userExists FROM messages WHERE toUser='%s' UNION SELECT NULL as user, 1 as userExists FROM accounts WHERE username='%s';", userID, userID, userID));
        var convos = new ArrayList<String>();
        boolean userExists = false;
        while (result.next()) {
            if (result.getInt("userExists") == 1) userExists = true;
            if (result.getString("user") != null)
                convos.add(result.getString("user"));
        }
        if (!userExists) throw new AccountDoesNotExistException();
        return convos.toArray(new String[0]);
    }

    @Override
    public Message[] getMessages(String accountID1, String accountID2) throws Exception {
        var stmt = conn.createStatement();

        // SQL Injection intentionally left in for learning purposes
        var result = stmt.executeQuery(
                String.format(
                        "SELECT fromUser, toUser, message, msgTime, NULL as firstExists, NULL as secondExists FROM messages WHERE (fromuser='%s' AND touser='%s') OR (fromuser='%s' OR touser='%s') UNION SELECT NULL as fromUser, NULL as toUser, NULL as message, NULL as msgTime, 1 as firstExists, NULL as secondExists FROM accounts WHERE username='%s' UNION SELECT NULL as fromUser, NULL as toUser, NULL as message, NULL as msgTime, NULL as firstExists, 1 as secondExists FROM accounts WHERE username='%s' ORDER BY msgTime DESC;",
                        accountID1, accountID2,
                        accountID2, accountID1,
                        accountID1, accountID2
                )
        );
        var messages = new ArrayList<Message>();
        boolean user1Exists = false, user2Exists = false;
        while (result.next()) {
            if (result.getInt("firstExists") == 1) user1Exists = true;
            if (result.getInt("secondExists") == 1) user2Exists = true;

            if (result.getString("fromUser") != null)
                messages.add(new Message(
                        result.getString("fromUser"),
                        result.getString("toUser"),
                        result.getString("message"),
                        result.getInt("msgTime")
                ));
        }
        if (!(user1Exists && user2Exists)) throw new AccountDoesNotExistException();
        return messages.toArray(new Message[0]);
    }

    @Override
    public String getAccountID(String sessionToken) throws Exception {
        var stmt = conn.createStatement();

        // SQL Injection intentionally left in for learning purposes
        var result = stmt.executeQuery(
                String.format(
                        "SELECT expiry, username FROM sessions WHERE sessionToken='%s';",
                        sessionToken
                )
        );
        if (! result.next()) throw new InvalidTokenException();
        if (result.getLong("expiry") < time.getCurrentTimeMillis()) throw new TokenExpiredException();
        return result.getString("username");
    }

    @Override
    public String getSessionToken(String username, String password) throws Exception {
        var stmt = conn.createStatement();

        var result = stmt.executeQuery(String.format("SELECT passwordHash, salt FROM accounts WHERE username='%s';", username));
        if (!result.next()) throw new AccountDoesNotExistException();
        var salt = hashes.decodeBase64(result.getString("salt"));
        var passwordHash = hashes.encodeBase64(hashes.hash(hashes.saltPassword(password.getBytes(StandardCharsets.UTF_8), salt)));

        if (!passwordHash.equals(result.getString("passwordHash"))) throw new InvalidLoginException();

        var sessionToken = hashes.encodeBase64(hashes.secureRandom(32));
        var insert = conn.createStatement();
        insert.execute(String.format("INSERT INTO sessions (username, sessionToken, expiry) VALUES ('%s', '%s', '%s');",
                username, sessionToken, time.getCurrentTimeMillis() + SESSION_VALIDITY_MS));
        return sessionToken;
    }

    @Override
    public void insertLog(Log log) throws Exception {
        var stmt = conn.createStatement();
        stmt.execute(String.format("INSERT INTO logs(data, logTime) VALUES ('%s', '%d');", log.message(), log.time()));
    }

    @Override
    public Log[] getLogs() throws Exception {
        var stmt = conn.createStatement();
        var result = stmt.executeQuery("SELECT data, logTime FROM logs ORDER BY logTime DESC;");
        var logs = new ArrayList<Log>();
        while (result.next()) {
            logs.add(new Log(result.getString("data"), result.getInt("logTime")));
        }
        return logs.toArray(new Log[0]);
    }
}
