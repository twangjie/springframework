package telnet.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import telnet.domain.Device;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 王杰 on 2017/7/4.
 */
public class TelnetExecutorFactory {

    private static final Log logger = LogFactory.getLog(TelnetExecutorFactory.class);

    private static TelnetExecutorFactory instance = new TelnetExecutorFactory();

    private ConcurrentHashMap<String, TelnetExecutor> telnetExecutorConcurrentHashMap = new ConcurrentHashMap();

    protected TelnetExecutorFactory() {

    }

    public static TelnetExecutorFactory getInstance() {
        return instance;
    }

    public TelnetExecutor createTelnetExecutor(String sessionId, String userId, Device device) {

        TelnetExecutor executor = null;
        if (!telnetExecutorConcurrentHashMap.containsKey(sessionId)) {
            executor = new TelnetExecutor();
            executor.setSessionId(sessionId);
            executor.setUserId(userId);
            executor.setDevice(device);

            telnetExecutorConcurrentHashMap.put(sessionId, executor);

        } else {
            executor = telnetExecutorConcurrentHashMap.get(sessionId);
        }

        return executor;
    }

    public TelnetExecutor getTelnetExecutor(String sessionId) {

        TelnetExecutor executor = null;
        if (telnetExecutorConcurrentHashMap.containsKey(sessionId)) {
            executor = telnetExecutorConcurrentHashMap.get(sessionId);
        }

        return executor;
    }

    public void releaseTelnetExecutor(String sessionId) {
        if (telnetExecutorConcurrentHashMap.containsKey(sessionId)) {

            TelnetExecutor executor = telnetExecutorConcurrentHashMap.get(sessionId);
            telnetExecutorConcurrentHashMap.remove(sessionId);
            executor.disconnect();
        }
    }
}
