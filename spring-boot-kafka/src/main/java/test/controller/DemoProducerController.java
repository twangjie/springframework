package test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoProducerController {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @RequestMapping(path = "/produce", method = RequestMethod.POST)
    public String produce(@RequestBody String data) {
        kafkaTemplate.send("test2", "demo", data);
        return "{\"status\":\"Ok\"}";
    }
}
