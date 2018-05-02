package net.sf.log4jdbc;

public class SpyLogFactory {
    private static final SpyLogDelegator logger = new MineSlf4jSpyLogDelegator();

    private SpyLogFactory() {
    }

    public static SpyLogDelegator getSpyLogDelegator() {
        return logger;
    }
}
