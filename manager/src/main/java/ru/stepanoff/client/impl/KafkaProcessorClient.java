package ru.stepanoff.client.impl;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import ru.stepanoff.client.ProcessorClient;
import ru.stepanoff.configuration.ProducerKafkaConfig;
import ru.stepanoff.dto.ConsumerRuleDTO;
import ru.stepanoff.dto.ProcessorRuleDTO;
import ru.stepanoff.dto.ProducerRuleDTO;
import ru.stepanoff.dto.serializer.ProcessorRuleDTOSerializer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class KafkaProcessorClient implements ProcessorClient {
    private final KafkaProducer<String, ProcessorRuleDTO> kafkaProducer;
    private final String outputTopic;

    public KafkaProcessorClient(ProducerKafkaConfig kafkaConfig) {
        kafkaProducer = new KafkaProducer<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers(),
                        ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString(),
                        ProducerConfig.ACKS_CONFIG, kafkaConfig.getAcks()
                ),
                new StringSerializer(),
                new ProcessorRuleDTOSerializer()
        );
        outputTopic = kafkaConfig.getProcessorTopic();
    }

    @PreDestroy
    void close() {
        kafkaProducer.close();
    }

    @Override
    public void sendRule(ProcessorRuleDTO processorRuleDTO) {
        log.debug("Отправляю в топик {} объект {}", outputTopic, processorRuleDTO.toString());
        kafkaProducer.send(new ProducerRecord<>(outputTopic, processorRuleDTO));
    }
}
