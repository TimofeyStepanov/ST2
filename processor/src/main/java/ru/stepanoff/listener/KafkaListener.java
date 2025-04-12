package ru.stepanoff.listener;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;
import ru.stepanoff.config.ConsumerKafkaConfig;
import ru.stepanoff.dto.ProcessorRuleDTO;
import ru.stepanoff.dto.deserializer.ProcessorRuleDTODeserializer;
import ru.stepanoff.service.ProcessorService;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class    KafkaListener {
    private final ConsumerKafkaConfig kafkaConfig;
    private final ProcessorService processorService;
    private KafkaConsumer<String, ProcessorRuleDTO> kafkaConsumer;

    @PostConstruct
    void startListening() {
        CompletableFuture
                .runAsync(() -> {
                    while (true) {
                        try {
                            init();
                            log.debug("Начинаю слушать сообщения из Кафки для обновления правил");
                            while (true) {
                                ConsumerRecords<String, ProcessorRuleDTO> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
                                for (ConsumerRecord<String, ProcessorRuleDTO> consumerRecord : consumerRecords) {
                                    log.debug("Сообщение из топика Кафки {} : {}", consumerRecord.topic(), consumerRecord.value().toString());
                                    processKafkaRecord(consumerRecord);
                                }
                            }
                        } catch (Exception e) {
                            log.error("Ошибка: {}, причина: {}", e.getMessage(), e.getCause().getMessage());
                        }
                    }

                });
    }

    private void init() {
        kafkaConsumer = new KafkaConsumer<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroupId(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getAutoOffsetReset()
                ),
                new StringDeserializer(),
                new ProcessorRuleDTODeserializer()
        );
        kafkaConsumer.subscribe(Collections.singleton(kafkaConfig.getInTopic()));
    }

    private void processKafkaRecord(ConsumerRecord<String, ProcessorRuleDTO> consumerRecord) {
        processorService.updateProcessorRule(consumerRecord.value());
    }
}
