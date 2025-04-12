package ru.stepanoff.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ConsumerKafkaConfig {
    @Value("${processor.kafka.bootstrapServices}")
    private String bootstrapServers;

    @Value("${processor.kafka.groupId}")
    private String groupId;

    @Value("${processor.kafka.autoOffsetReset}")
    private String autoOffsetReset;

    @Value("${processor.kafka.inTopic}")
    private String inTopic;
}
