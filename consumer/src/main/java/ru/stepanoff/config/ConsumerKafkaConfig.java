package ru.stepanoff.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ConsumerKafkaConfig {

    @Value("${consumer.kafka.bootstrapServices}")
    private String bootstrapServers;

    @Value("${consumer.kafka.groupId}")
    private String groupId;

    @Value("${consumer.kafka.autoOffsetReset}")
    private String autoOffsetReset;

    @Value("${consumer.kafka.inTopic}")
    private String inTopic;
}
