package ru.stepanoff.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import ru.stepanoff.client.KafkaClient;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserKafkaKafkaClient implements KafkaClient {
    private final AtomicReference<KafkaProducer<String, String>> kafkaProducer = new AtomicReference<>();
    private Set<String> outTopics = new HashSet<>();

    @Override
    public void sendAsync(String message) {
        outTopics.forEach(outTopic -> {
            log.debug("Отправляю в топик {} сообщение: {}", outTopic, message);
            kafkaProducer.get().send(new ProducerRecord<>(outTopic, message));
        });
    }

    public void updateConfig(Map<String, Object> kafkaConfig, Set<String> topics) {
        outTopics = topics;
        kafkaProducer.set(new KafkaProducer<>(
                kafkaConfig,
                new StringSerializer(),
                new StringSerializer()
        ));
    }
}
