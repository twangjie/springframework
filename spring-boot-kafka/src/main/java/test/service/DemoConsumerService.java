package test.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DemoConsumerService /*implements MessageListener<String,String>*/ {

    private static Log logger = LogFactory.getLog(DemoConsumerService.class);

//    @Override
//    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
//        System.out.println("get message :" + consumerRecord.value());
//    }

    @KafkaListener(topics = "${spring.kafka.topic.test}")
    public void processMessage(ConsumerRecord<?, ?> record) {
        //System.out.println(record.toString());
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            //System.out.println("this is the testTopic send message:" + message);
            logger.info(String.format("Received: [%s] %s", record.key(), message));
        }
    }
}