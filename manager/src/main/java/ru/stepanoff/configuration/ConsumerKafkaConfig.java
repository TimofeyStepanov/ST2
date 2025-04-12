package ru.stepanoff.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ConsumerKafkaConfig {
    @Value("${manager.kafka.bootstrapServices}")
    private String bootstrapServers;

    @Value("${manager.kafka.groupId}")
    private String groupId;

    @Value("${manager.kafka.autoOffsetReset}")
    private String autoOffsetReset;

    @Value("${manager.kafka.consumerInTopic}")
    private String consumerTopic;

    @Value("${manager.kafka.processorInTopic}")
    private String processorTopic;

    @Value("${manager.kafka.producerInTopic}")
    private String producerTopic;
}
