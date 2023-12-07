package com.br.spring.springkafka;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmbeddedKafkaTest extends AbstractEmbeddedKafkaTest {

    @Test
    void test() {
        Assertions.assertNotNull(getProducer());
    }
}
