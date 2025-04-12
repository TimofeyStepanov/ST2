package ru.stepanoff.listener;

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
import ru.stepanoff.configuration.ConsumerKafkaConfig;
import ru.stepanoff.service.ManagerService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static ru.stepanoff.constants.MessagesEnum.RULE_REPEAT;
import static ru.stepanoff.constants.MessagesEnum.SOS;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaListener {

    private final ConsumerKafkaConfig kafkaConfig;

    private KafkaConsumer<String, String> managerKafkaReader;

    private final ManagerService managerService;
    private final Map<String, Map<String, Consumer<String>>> topicAndItsMessagesConsumer = new HashMap<>();


    @EventListener({ContextRefreshedEvent.class})
    private void startListening() {
        init();
        CompletableFuture
                .runAsync(() -> {
                    while (true) {
                        ConsumerRecords<String, String> consumerRecords = managerKafkaReader.poll(Duration.ofMillis(100));
                        for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                            log.debug("Сообщение из топика Кафки {} : {}", consumerRecord.topic(), consumerRecord.value());
                            processKafkaRecord(consumerRecord);
                        }
                    }
                })
                .thenRun(() -> {
                    throw new IllegalStateException("Проблемы c чтением данных из топиков");
                });
    }

    private void init() {
        managerKafkaReader = new KafkaConsumer<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroupId(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfig.getAutoOffsetReset()
                ),
                new StringDeserializer(),
                new StringDeserializer()
        );
        managerKafkaReader.subscribe(
                List.of(kafkaConfig.getConsumerTopic(),
                        kafkaConfig.getProcessorTopic(),
                        kafkaConfig.getProducerTopic())
        );


        String consumerTopic = kafkaConfig.getConsumerTopic();
        topicAndItsMessagesConsumer.put(consumerTopic, new HashMap<>());
        topicAndItsMessagesConsumer.get(consumerTopic).put(SOS.message, msg -> managerService.stopConsumer());
        topicAndItsMessagesConsumer.get(consumerTopic).put(RULE_REPEAT.message, msg -> managerService.updateConsumerWithTheLatestConfigurationIfItExists());

        String processorTopic = kafkaConfig.getProcessorTopic();
        topicAndItsMessagesConsumer.put(processorTopic, new HashMap<>());
        topicAndItsMessagesConsumer.get(processorTopic).put(RULE_REPEAT.message, msg -> managerService.updateProcessorWithTheLatestConfigurationIfItExists());

        String producerTopic = kafkaConfig.getProducerTopic();
        topicAndItsMessagesConsumer.put(producerTopic, new HashMap<>());
        topicAndItsMessagesConsumer.get(producerTopic).put(RULE_REPEAT.message, msg -> managerService.updateProducerWithTheLatestConfigurationIfItExists());
    }

    private void processKafkaRecord(ConsumerRecord<String, String> consumerRecord) {
        String topic = consumerRecord.topic();
        if (!topicAndItsMessagesConsumer.containsKey(topic)) {
            log.warn("Неизвестный топик {}", topic);
        }

        String message = consumerRecord.value();
        Consumer<String> messageConsumer = topicAndItsMessagesConsumer
                .get(topic)
                .getOrDefault(message, msg -> log.warn("Неизвестное сообщение из топика {}: {}", topic, msg));
        messageConsumer.accept(message);
    }

}
