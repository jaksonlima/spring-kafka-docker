package com.br.spring.springkafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

@EmbeddedKafka(partitions = 1)
@ActiveProfiles("application-test-integration")
@SpringBootTest(
        properties = {"kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"}
)
@Tag("integrationTest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractEmbeddedKafkaTest {

    private Producer<String, String> producer;

    @Autowired
    protected EmbeddedKafkaBroker kafkaBroker;

    @BeforeAll
    void init() {
        producer = new DefaultKafkaProducerFactory<>(
                KafkaTestUtils.producerProps(kafkaBroker),
                new StringSerializer(),
                new StringSerializer()
        ).createProducer();
    }

    @AfterAll
    void shutdown() {
        producer.close();
    }

    public Producer<String, String> getProducer() {
        return producer;
    }
}
