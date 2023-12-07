package com.br.spring.springkafka;


import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
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
    @RetryableTopic(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            attempts = "4",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    public void onMessage(@Payload final Object message, final ConsumerRecordMetadata metadata) {
        System.out.println("Received Message: " + message);
        System.out.println("Received topic: " + metadata.topic());
        System.out.println("Received partition: " + metadata.partition());
        System.out.println("Received offset: " + metadata.offset());
    }

    /**
     * Depois attempts = "4"
     * sera redirecionado para @DltHandler
     * para jogar para outra fila ou sua regra de negocio
     * */
    @DltHandler
    public void onDLTMessage(@Payload final Object message, final ConsumerRecordMetadata metadata) {
        System.out.println("Cancel");
        System.out.println("Received Message: " + message);
        System.out.println("Received topic: " + metadata.topic());
        System.out.println("Received partition: " + metadata.partition());
        System.out.println("Received offset: " + metadata.offset());
    }

}
