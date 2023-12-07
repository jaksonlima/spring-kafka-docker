package com.br.spring.springkafka;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaComponentListener {

    @KafkaListener(
            concurrency = "${spring.kafka.consumers.input.concurrency}",
            containerFactory = "kafkaListenerFactory",
            topics = "${spring.kafka.consumers.input.topics}",
            groupId = "${spring.kafka.consumers.input.group-id}",
            id = "${spring.kafka.consumers.input.id}",
            properties = {
                    "auto.offset.reset=${spring.kafka.consumers.input.auto-offset-reset}"
            }
    )
    public void listenGroup(@Payload final Object message, final ConsumerRecordMetadata metadata) {
        System.out.println("Received Message: " + message);
        System.out.println("Received topic: " + metadata.topic());
        System.out.println("Received partition: " + metadata.partition());
        System.out.println("Received offset: " + metadata.offset());
    }

}
