package shared;

public class SystemTimeFetcher implements TimeFetcher {
    @Override
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
