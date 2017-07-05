package telnet.service.service.impl;

import telnet.service.TelnetWebSocketHandlerDecoratorFactory;
import telnet.service.executor.TelnetExecutor;
import telnet.service.executor.TelnetExecutorFactory;
import telnet.service.model.TelnetCommand;
import telnet.service.model.TelnetConnect;
import telnet.service.model.TelnetResponse;
import telnet.service.service.TelnetService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 王杰 on 2017/7/4.
 */
@Service
public class TelnetServiceImpl implements TelnetService {

    private static final Log logger = LogFactory.getLog(TelnetServiceImpl.class);


    @Override
    public TelnetResponse executeTelnetConnect(String sessionId, TelnetConnect command) {

        String userId = command.getUserId();

        TelnetResponse response = new TelnetResponse();
        response.setUserId(userId);

        TelnetExecutor executor = TelnetExecutorFactory.getInstance().createTelnetExecutor(sessionId, userId, command.getIpaddr(), command.getPort());

        if(!executor.connect()){
            TelnetWebSocketHandlerDecoratorFactory.getInstance().closeSession(sessionId);
        }

        String ret = executor.readResponse();
        response.setContent(ret);

        return response;
    }

    @Override
    public TelnetResponse executeTelnetDisconnect(String sessionId, TelnetConnect command) {

        String userId = command.getUserId();

        TelnetResponse response = new TelnetResponse();
        response.setUserId(userId);

        TelnetExecutorFactory.getInstance().releaseTelnetExecutor(sessionId);

        return response;
    }

    @Override
    public TelnetResponse executeTelnetCommand(String sessionId, TelnetCommand command) {

        String userId = command.getUserId();

        TelnetResponse response = new TelnetResponse();
        response.setUserId(userId);

        TelnetExecutor executor = TelnetExecutorFactory.getInstance().getTelnetExecutor(sessionId);
        if (executor != null) {
            response.setContent(executor.sendCommand(command.getContent()));
        }
        return response;
    }

    @Scheduled(fixedDelay=1500)
    public void sendTradeNotifications() {

        Map<String, Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

//        for (TelnetResult result : this.telnetResults) {
//            if (System.currentTimeMillis() >= (result.timestamp + 1500)) {
//                logger.debug("Sending position update: ");
//                this.simpMessageSendingOperations.convertAndSendToUser(result.user, "/telnet", result.result, map);
//                this.telnetResults.remove(result);
//            }
//        }
    }

    private static class TelnetResult {

        private final String user;
        private final long timestamp;
        private final String result;

        public TelnetResult(String user, String result) {
            this.user = user;
            this.result = result;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
