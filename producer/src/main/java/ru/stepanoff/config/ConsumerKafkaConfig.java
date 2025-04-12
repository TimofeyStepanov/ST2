package ru.stepanoff.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ConsumerKafkaConfig {
    @Value("${producer.kafka.bootstrapServices}")
    private String bootstrapServers;

    @Value("${producer.kafka.groupId}")
    private String groupId;

    @Value("${producer.kafka.autoOffsetReset}")
    private String autoOffsetReset;

    @Value("${producer.kafka.inTopic}")
    private String inTopic;
}
