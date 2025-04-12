package ru.stepanoff.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@Component
public class ProducerKafkaConfig {
    private String clientIdConfig = UUID.randomUUID().toString();

    @Value("${producer.kafka.bootstrapServices}")
    private String bootstrapServers;

    @Value("${producer.kafka.requestTimeoutMs}")
    private String requestTimeoutMsConfig;

    @Value("${producer.kafka.batchSize}")
    private String batchSize;

    @Value("${producer.kafka.bufferMemory}")
    private String bufferMemoryConfig;

    @Value("${producer.kafka.lingerMs}")
    private String lingerMsConfig;

    @Value("${producer.kafka.acks}")
    private String acks;

    @Value("${producer.kafka.outTopic}")
    private String outTopic;
}
