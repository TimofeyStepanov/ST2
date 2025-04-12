package ru.stepanoff.client.impl;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import ru.stepanoff.client.ConsumerClient;
import ru.stepanoff.configuration.ProducerKafkaConfig;
import ru.stepanoff.dto.ConsumerRuleDTO;
import ru.stepanoff.dto.serializer.ConsumerRuleDTOSerializer;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class KafkaConsumerClient implements ConsumerClient {
    private final KafkaProducer<String, ConsumerRuleDTO> kafkaProducer;
    private final String outputTopic;

    public KafkaConsumerClient(ProducerKafkaConfig kafkaConfig) {
        kafkaProducer = new KafkaProducer<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers(),
                        ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.getClientIdConfig(),
                        ProducerConfig.ACKS_CONFIG, kafkaConfig.getAcks()
                ),
                new StringSerializer(),
                new ConsumerRuleDTOSerializer()
        );
        outputTopic = kafkaConfig.getConsumerTopic();
    }

    @PreDestroy
    void close() {
        kafkaProducer.close();
    }

    @Override
    public void sendRule(ConsumerRuleDTO consumerRuleDTO) {
        log.debug("Отправляю в топик {} объект {}", outputTopic, consumerRuleDTO.toString());
        kafkaProducer.send(new ProducerRecord<>(outputTopic, consumerRuleDTO));
    }
}
