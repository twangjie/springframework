package test.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import test.model.EchoMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class Echo3Controller {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @MessageMapping("/echo3/hello")
    @SendTo("/topic/echo3/greetings")
    public EchoMessage greeting(EchoMessage message) throws Exception {
        Thread.sleep(100); // simulated delay

        message.setMessage(simpleDateFormat.format(new Date()) + ": " + message.getMessage());

        return message;
    }

}
