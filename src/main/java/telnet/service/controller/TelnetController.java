package telnet.service.controller;

import org.springframework.messaging.handler.annotation.Headers;
import telnet.service.model.TelnetCommand;
import telnet.service.model.TelnetConnect;
import telnet.service.model.TelnetResponse;
import telnet.service.service.TelnetService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class TelnetController {


    private static final Log logger = LogFactory.getLog(TelnetController.class);

    private final TelnetService telnetService;

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    public TelnetController(TelnetService telnetService) {
        this.telnetService = telnetService;
    }


    private String getSessionId(final Map<String, Object> headers) {

        String sessionId = "";

        if (headers.containsKey("simpSessionId")) {
            sessionId = (String) headers.get("simpSessionId");
        }

        return sessionId;
    }

    @MessageMapping("/telnet/connect")
    public void handleConnect(TelnetConnect message, @Headers final Map<String, Object> headers) {

//        simpMessageSendingOperations.convertAndSendToUser(message.getUserId(), "/telnet", message);
        TelnetResponse response = telnetService.executeTelnetConnect(getSessionId(headers), message);
        simpMessageSendingOperations.convertAndSendToUser(response.getUserId(), "/telnet/cmdresp", response);
    }

    @MessageMapping("/telnet/disconnect")
    public void handleDisconnect(TelnetConnect message, @Headers final Map<String, Object> headers) {

//        simpMessageSendingOperations.convertAndSendToUser(message.getUserId(), "/telnet", message);
        TelnetResponse response = telnetService.executeTelnetDisconnect(getSessionId(headers), message);
        simpMessageSendingOperations.convertAndSendToUser(response.getUserId(), "/telnet/cmdresp", response);
    }

    @MessageMapping("/telnet/command")
    public void handleCommand(TelnetCommand message, @Headers final Map<String, Object> headers) {

        TelnetResponse response = telnetService.executeTelnetCommand(getSessionId(headers), message);
        if(response.getContent().length() > 0) {
            simpMessageSendingOperations.convertAndSendToUser(response.getUserId(), "/telnet/cmdresp", response);
        }
    }

    @MessageMapping("/telnet/tab")
    public void handleTab(TelnetCommand message, @Headers final Map<String, Object> headers) {

        TelnetResponse response = telnetService.executeTelnetCommand(getSessionId(headers), message);
        if(response.getContent().length() > 0) {
            simpMessageSendingOperations.convertAndSendToUser(response.getUserId(), "/telnet/tabresp", response);
        }
    }

    @MessageMapping("/telnet/keydown")
    public synchronized void handleKeydown(TelnetCommand message, @Headers final Map<String, Object> headers) {

        TelnetResponse response = telnetService.executeTelnetKeydown(getSessionId(headers), message);
        if(response.getContent().length() > 0) {
            simpMessageSendingOperations.convertAndSendToUser(response.getUserId(), "/telnet/keydownresp", response);
        }
    }
}
