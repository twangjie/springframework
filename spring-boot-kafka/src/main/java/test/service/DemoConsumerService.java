package test.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DemoConsumerService /*implements MessageListener<String,String>*/ {

    private static Log logger = LogFactory.getLog(DemoConsumerService.class);

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//    @Override
//    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
//        System.out.println("get message :" + consumerRecord.value());
//    }

    @KafkaListener(topics = "${spring.kafka.topic.test}")
    public void processMessage(ConsumerRecord<String, String> record) {
        if (record.value().length() > 0) {
            logger.info(String.format("Received: [%s] %s", record.key(), record.value()));

            //String jsonData = "{\"key\":\"" + record.key() + "\",\"value\":" + record.value() +"}";

            this.simpMessageSendingOperations.convertAndSend("/topic/tip/v2/monctrl/alarms", record.value());
        }
    }


    @Scheduled(cron = "0/10 * * * * ?")
    public void produceAlarms() {

        String data = "{\"taskid\":1,\"plate\":\"川A12345\",\"type\":\"盗抢车\",\"creatorid\":\"X123465\"," +
                "\"creatorname\":\"张三\",\"firstalarmtime\":\"2017-9-10 15:16:49\"," +
                "\"firstalarmdev\":\"3401123456780\",\"lastalarmtime\":\"" + sdf.format(new Date()) + "\"," +
                "\"alarmcount\":2,\"alarmids\":[1,2],\"ids\":[\"12312\",\"456234213\"]}";

        kafkaTemplate.send("test2", "demo", data);
    }
}