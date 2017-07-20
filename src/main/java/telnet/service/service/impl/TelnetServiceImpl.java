package telnet.service.service.impl;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import telnet.service.TelnetWebSocketHandlerDecoratorFactory;
import telnet.service.domain.Device;
import telnet.service.executor.TelnetExecutor;
import telnet.service.executor.TelnetExecutorFactory;
import telnet.service.model.TelnetCommand;
import telnet.service.model.TelnetConnect;
import telnet.service.model.TelnetResponse;
import telnet.service.service.DeviceService;
import telnet.service.service.TelnetService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by 王杰 on 2017/7/4.
 */
@Service
public class TelnetServiceImpl implements TelnetService {

    private static final Log logger = LogFactory.getLog(TelnetServiceImpl.class);

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    private DeviceService deviceService;

//    private ConcurrentLinkedQueue<TelnetResult> telnetResults = new ConcurrentLinkedQueue();

    @Override
    public void executeTelnetConnect(String sessionId, TelnetConnect command) {

        String userId = command.getUserId();

        TelnetResponse response = new TelnetResponse();
        response.setUserId(userId);

        Device device = deviceService.findById(command.getDeviceId());

        TelnetExecutor executor = TelnetExecutorFactory.getInstance().createTelnetExecutor(sessionId, userId, device);
        executor.setDeviceService(deviceService);
        executor.setSimpMessageSendingOperations(simpMessageSendingOperations);

//        String ret = "";
        if (!executor.connect()) {
            TelnetWebSocketHandlerDecoratorFactory.getInstance().closeSession(sessionId);
        }
//        else {
//            ret = executor.readResponse();
//        }
//
//        response.setContent(ret);
//
//        if (response.getContent().length() > 0) {
//            simpMessageSendingOperations.convertAndSendToUser(response.getUserId(), "/telnet/cmdresp", response);
//        }

    }

    @Override
    public void executeTelnetDisconnect(String sessionId, TelnetConnect command) {

        String userId = command.getUserId();

        TelnetResponse response = new TelnetResponse();
        response.setUserId(userId);

        TelnetWebSocketHandlerDecoratorFactory.getInstance().closeSession(sessionId);

        if (response.getContent().length() > 0) {
            simpMessageSendingOperations.convertAndSendToUser(response.getUserId(), "/telnet/cmdresp", response);
        }
    }

    @Override
    public void executeTelnetCommand(String sessionId, TelnetCommand command) {

        String userId = command.getUserId();

        TelnetResponse response = new TelnetResponse();
        response.setUserId(userId);

        TelnetExecutor executor = TelnetExecutorFactory.getInstance().getTelnetExecutor(sessionId);
        if (executor != null) {
//            response.setContent(executor.sendCommand(command.getContent()));
            executor.sendCommand(command.getContent());
        }

//        if (response.getContent().length() > 0) {
//            simpMessageSendingOperations.convertAndSendToUser(response.getUserId(), "/telnet/cmdresp", response);
//        }
    }

    @Override
    public void executeTelnetTab(String sessionId, TelnetCommand command) {
        executeTelnetCommand(sessionId, command);
    }

    @Override
    public void executeTelnetKeydown(String sessionId, TelnetCommand command) {
        executeTelnetCommand(sessionId, command);
    }

    @Scheduled(fixedDelay = 5000)
    public void sendTradeNotifications() {

//        Map<String, Object> map = new HashMap<>();
//        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
//
//        for (TelnetResult result : this.telnetResults) {
////            logger.debug("Sending position update: ");
//            this.simpMessageSendingOperations.convertAndSendToUser(result.user, result.respEndpoint, result.result, map);
//            this.telnetResults.remove(result);
//        }
    }

    private static class TelnetResult {

        private final String user;
        private final long timestamp;
        private final String result;
        private final String respEndpoint;

        public TelnetResult(String user, String result, String respEndpoint) {
            this.user = user;
            this.result = result;
            this.respEndpoint = respEndpoint;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
