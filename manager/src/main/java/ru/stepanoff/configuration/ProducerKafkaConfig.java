package ru.stepanoff.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Data
@Configuration
public class ProducerKafkaConfig {
    @Value("${manager.kafka.bootstrapServices}")
    private String bootstrapServers;

    @Value("${manager.kafka.consumerOutTopic}")
    private String consumerTopic;

    @Value("${manager.kafka.processorOutTopic}")
    private String processorTopic;

    @Value("${manager.kafka.producerOutTopic}")
    private String producerTopic;

    private String clientIdConfig = UUID.randomUUID().toString();

    @Value("${manager.kafka.acks}")
    private String acks;
}
