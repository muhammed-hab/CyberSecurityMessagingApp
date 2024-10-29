package shared;

public record Message(String fromAccountID, String toAccountID, String content, long time) {}
