package ru.stepanoff.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Data
@Configuration
public class ProducerKafkaConfig {
    private final String clientIdConfig = UUID.randomUUID().toString();

    @Value("${consumer.kafka.bootstrapServices}")
    private String bootstrapServers;

    @Value("${consumer.kafka.requestTimeoutMs}")
    private String requestTimeoutMsConfig;

    @Value("${consumer.kafka.batchSize}")
    private String batchSize;

    @Value("${consumer.kafka.bufferMemory}")
    private String bufferMemoryConfig;

    @Value("${consumer.kafka.lingerMs}")
    private String lingerMsConfig;

    @Value("${consumer.kafka.acks}")
    private String acks;

    @Value("${consumer.kafka.outTopic}")
    private String outTopic;
}
