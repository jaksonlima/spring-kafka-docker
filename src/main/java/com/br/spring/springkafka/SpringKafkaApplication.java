package com.br.spring.springkafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Objects;

@SpringBootApplication
public class SpringKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringKafkaApplication.class, args);
    }

    @Component
    public static class ListenGroup {
        @KafkaListener(topics = "input", groupId = "spring-with-kafka")
        public void listenGroup(@Payload Object message, @Headers Object headers) {
            System.out.println("Received Message: " + message + "from partition: " + headers);
        }
    }

    @RestController
    public static class Controller {
        private final KafkaTemplate<String, Object> kafkaTemplate;

        public Controller(final KafkaTemplate<String, Object> kafkaTemplate) {
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

    //@Configuration
    public static class KafkaTopicConfig {
        @Value("${spring.kafka.bootstrap-servers}")
        private String bootstrapAddress;

        /**
         * Auto confiuração spring boot
         *
         * @link {org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration}
         */
        @Bean
        public KafkaAdmin kafkaAdmin() {
            final var configs = new HashMap<String, Object>();
            configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

            return new KafkaAdmin(configs);
        }
    }

    @Configuration
    public static class KafkaTopic {
        @Bean
        public NewTopic input() {
            return new NewTopic("input", 1, (short) 1);
        }

        @Bean
        public NewTopic out() {
            return new NewTopic("out", 1, (short) 1);
        }
    }

    @Configuration
    public static class KafkaProducerConfig {
        @Value("${spring.kafka.bootstrap-servers}")
        private String bootstrapAddress;

        @Bean
        public ProducerFactory<String, Object> producerFactory() {
            final var configProps = new HashMap<String, Object>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, Object> kafkaTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }
    }

    @EnableKafka
    @Configuration
    public class KafkaConsumerConfig {
        @Value("${spring.kafka.bootstrap-servers}")
        private String bootstrapAddress;

        @Bean
        public ConsumerFactory<String, String> consumerFactory() {
            final var groupId = "spring-with-kafka";
            final var props = new HashMap<String, Object>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

            return new DefaultKafkaConsumerFactory<>(props, new JsonDeserializer<>(), new JsonDeserializer<>());
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, String>
        kafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, String> factory =
                    new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory());

            return factory;
        }
    }

}
