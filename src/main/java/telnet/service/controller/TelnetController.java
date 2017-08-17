package telnet.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import telnet.service.model.TelnetCommand;
import telnet.service.model.TelnetConnect;
import telnet.service.service.TelnetService;

import java.util.Map;

@Controller
@MessageMapping("telnet")
public class TelnetController {

    private final TelnetService telnetService;

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

    @MessageMapping("/connect")
    public void handleConnect(TelnetConnect message, @Headers final Map<String, Object> headers) {

        telnetService.executeTelnetConnect(getSessionId(headers), message);
    }

    @MessageMapping("/disconnect")
    public void handleDisconnect(TelnetConnect message, @Headers final Map<String, Object> headers) {
        telnetService.executeTelnetDisconnect(getSessionId(headers), message);
    }

    @MessageMapping("/command")
    public void handleCommand(TelnetCommand message, @Headers final Map<String, Object> headers) {
        telnetService.executeTelnetCommand(getSessionId(headers), message);
    }

    @MessageMapping("/tab")
    public void handleTab(TelnetCommand message, @Headers final Map<String, Object> headers) {

        telnetService.executeTelnetCommand(getSessionId(headers), message);
    }

    @MessageMapping("/keydown")
    public synchronized void handleKeydown(TelnetCommand message, @Headers final Map<String, Object> headers) {

        telnetService.executeTelnetKeydown(getSessionId(headers), message);
    }
}
