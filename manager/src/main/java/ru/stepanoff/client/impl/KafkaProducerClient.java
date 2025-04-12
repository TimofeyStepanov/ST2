package ru.stepanoff.client.impl;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import ru.stepanoff.client.ProducerClient;
import ru.stepanoff.configuration.ProducerKafkaConfig;
import ru.stepanoff.dto.ProducerRuleDTO;
import ru.stepanoff.dto.serializer.ProducerRuleDTOSerializer;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class KafkaProducerClient implements ProducerClient {
    private final KafkaProducer<String, ProducerRuleDTO> kafkaProducer;
    private final String outputTopic;


    public KafkaProducerClient(ProducerKafkaConfig kafkaConfig) {
        kafkaProducer = new KafkaProducer<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers(),
                        ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString(),
                        ProducerConfig.ACKS_CONFIG, kafkaConfig.getAcks()
                ),
                new StringSerializer(),
                new ProducerRuleDTOSerializer()
        );
        outputTopic = kafkaConfig.getProducerTopic();
    }

    @PreDestroy
    void close() {
        kafkaProducer.close();
    }

    @Override
    public void sendRule(ProducerRuleDTO producerRuleDTO) {
        log.debug("Отправляю в топик {} объект {}", outputTopic, producerRuleDTO.toString());
        kafkaProducer.send(new ProducerRecord<>(outputTopic, producerRuleDTO));
    }
}
