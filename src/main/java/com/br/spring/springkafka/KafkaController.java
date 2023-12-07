package com.br.spring.springkafka;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class KafkaController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaController(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
    }

    @PostMapping("send-message")
    public ResponseEntity<?> sendMessage(@RequestBody Object body) {
        final var input = kafkaTemplate.send("input", body);

        input.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Send message [" + body.toString() + "]" + " with offset [" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message [" + body.toString() + "]" + " due to [" + ex.getMessage() + "]");
            }
        });

        return ResponseEntity.noContent().build();
    }

}
