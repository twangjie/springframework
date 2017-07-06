package telnet.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import telnet.service.executor.TelnetExecutorFactory;
import telnet.service.service.DeviceService;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 王杰 on 2017/7/4.
 */
public class TelnetWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    private static final Log logger = LogFactory.getLog(TelnetWebSocketHandlerDecoratorFactory.class);

    private static TelnetWebSocketHandlerDecoratorFactory instance = new TelnetWebSocketHandlerDecoratorFactory();
    private ConcurrentHashMap<String, WebSocketSession> webSocketSessionConcurrentHashMap = new ConcurrentHashMap<>();


    public static TelnetWebSocketHandlerDecoratorFactory getInstance() {
        return instance;
    }

    protected TelnetWebSocketHandlerDecoratorFactory() {

    }

    public void closeSession(String sessionId) {
        if(webSocketSessionConcurrentHashMap.containsKey(sessionId)) {
            WebSocketSession webSocketSession = webSocketSessionConcurrentHashMap.get(sessionId);
            try {
                webSocketSession.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public WebSocketHandler decorate(WebSocketHandler webSocketHandler) {
        return new WebSocketHandlerDecorator(webSocketHandler) {
            @Override
            public void afterConnectionEstablished(final WebSocketSession session) throws Exception {

                String sessionId = session.getId();

                logger.debug(String.format("WebSocket client %s connected. sessionId: %s", session.getRemoteAddress().toString(), sessionId));

                webSocketSessionConcurrentHashMap.put(sessionId, session);

                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

                String sessionId = session.getId();
                logger.debug(String.format("WebSocket client %s disconnected. sessionId: %s. %s",
                        session.getRemoteAddress().toString(), sessionId, closeStatus.toString()));

                if(webSocketSessionConcurrentHashMap.containsKey(sessionId)) {
                    webSocketSessionConcurrentHashMap.remove(sessionId);
                }

                TelnetExecutorFactory.getInstance().releaseTelnetExecutor(sessionId);

                super.afterConnectionClosed(session, closeStatus);
            }
        };
    }
}
