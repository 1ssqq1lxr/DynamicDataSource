package net.sf.log4jdbc;

public class MineSlf4jSpyLogDelegator extends Slf4jSpyLogDelegator {
    public void sqlTimingOccured(Spy spy, long execTime, String methodCall, String sql) {
        char ch = sql.charAt(0);
        if (ch == 'i' || ch == 'I') {
            sql = sql.trim().toUpperCase();
            if (sql.startsWith("INSERT")) {
                return;
            }
        }
        super.sqlTimingOccured(spy, execTime, methodCall, sql);
    }
}
