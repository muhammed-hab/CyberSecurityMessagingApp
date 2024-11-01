CREATE TABLE IF NOT EXISTS accounts (
  username VARCHAR(20) PRIMARY KEY,
  passwordHash CHAR(44),
  salt CHAR(12)
);
CREATE TABLE IF NOT EXISTS messages (
  fromUser VARCHAR(20),
  toUser VARCHAR(20),
  message TEXT,
  msgTime INT,
  FOREIGN KEY (fromUser) REFERENCES accounts(username),
  FOREIGN KEY (toUser) REFERENCES accounts(username)
);
CREATE INDEX IF NOT EXISTS fromIdx ON messages (fromUser);
CREATE INDEX IF NOT EXISTS toIdx ON messages (toUser);
CREATE TABLE IF NOT EXISTS sessions (
  username VARCHAR(20),
  sessionToken CHAR(44) PRIMARY KEY,
  expiry INT,
  FOREIGN KEY (username) REFERENCES accounts(username)
);
CREATE TABLE IF NOT EXISTS logs (data TEXT, logTime INT);
create index if not exists logsTime on logs(logTime);