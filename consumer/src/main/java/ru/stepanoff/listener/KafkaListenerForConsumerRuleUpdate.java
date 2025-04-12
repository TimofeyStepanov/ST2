package ru.stepanoff.listener;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.stepanoff.config.ConsumerKafkaConfig;
import ru.stepanoff.dto.ConsumerRuleDTO;
import ru.stepanoff.dto.deserializer.ConsumerRuleDTODeserializer;
import ru.stepanoff.service.ConsumerService;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaListenerForConsumerRuleUpdate {

    private final ConsumerKafkaConfig kafkaConfig;
    private final ConsumerService consumerService;
    private KafkaConsumer<String, ConsumerRuleDTO> kafkaConsumer;

    @PostConstruct
    void startListening() {
        CompletableFuture
                .runAsync(() -> {
                    while (true) {
                        try {
                            init();
                            log.debug("Начинаю слушать сообщения из Кафки для обновления правил");
                            while (true) {
                                ConsumerRecords<String, ConsumerRuleDTO> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
                                for (ConsumerRecord<String, ConsumerRuleDTO> consumerRecord : consumerRecords) {
                                    log.debug("Сообщение из топика Кафки {} от менеджера : {}", consumerRecord.topic(), consumerRecord.value().toString());
                                    processKafkaRecord(consumerRecord);
                                }
                            }
                        } catch (Exception e) {
                            log.error("Ошибка: {}", e.getMessage());
                        }
                    }
                });
    }

    private void init() {
        kafkaConsumer = new KafkaConsumer<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroupId(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getAutoOffsetReset().toLowerCase()
                ),
                new StringDeserializer(),
                new ConsumerRuleDTODeserializer()
        );
        kafkaConsumer.subscribe(Collections.singleton(kafkaConfig.getInTopic()));
    }

    private void processKafkaRecord(ConsumerRecord<String, ConsumerRuleDTO> consumerRecord) {
        consumerService.updateConsumerRule(consumerRecord.value());
    }
}
