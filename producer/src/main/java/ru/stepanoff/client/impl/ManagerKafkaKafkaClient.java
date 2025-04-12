package ru.stepanoff.client.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import ru.stepanoff.client.KafkaClient;
import ru.stepanoff.config.ProducerKafkaConfig;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManagerKafkaKafkaClient implements KafkaClient {

    private final ProducerKafkaConfig kafkaConfig;
    private final AtomicReference<KafkaProducer<String, String>> kafkaProducer = new AtomicReference<>();
    private Set<String> outTopics = new HashSet<>();

    @PostConstruct
    private void init() {
        outTopics = Set.of(kafkaConfig.getOutTopic());
        kafkaProducer.set(new KafkaProducer<>(
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers(),
                        ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.getClientIdConfig(),

                        ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaConfig.getRequestTimeoutMsConfig(),
                        ProducerConfig.BATCH_SIZE_CONFIG, kafkaConfig.getBatchSize(),
                        ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaConfig.getBufferMemoryConfig(),
                        ProducerConfig.LINGER_MS_CONFIG, kafkaConfig.getLingerMsConfig(),
                        ProducerConfig.ACKS_CONFIG, kafkaConfig.getAcks()
                ),
                new StringSerializer(),
                new StringSerializer()
        ));
    }

    @Override
    public void sendAsync(String message) {
        outTopics.forEach(outTopic -> {
            log.debug("Отправляю в топик {} сообщение {}", outTopic, message);
            kafkaProducer.get().send(new ProducerRecord<>(outTopic, message));
        });
    }

}
