package ru.stepanoff.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Data
@Configuration
public class ProducerKafkaConfig {
    private final String clientIdConfig = UUID.randomUUID().toString();

    @Value("${processor.kafka.bootstrapServices}")
    private String bootstrapServers;

    @Value("${processor.kafka.requestTimeoutMs}")
    private String requestTimeoutMsConfig;

    @Value("${processor.kafka.batchSize}")
    private String batchSize;

    @Value("${processor.kafka.bufferMemory}")
    private String bufferMemoryConfig;

    @Value("${processor.kafka.lingerMs}")
    private String lingerMsConfig;

    @Value("${processor.kafka.acks}")
    private String acks;

    @Value("${processor.kafka.outTopic}")
    private String outTopic;
}
