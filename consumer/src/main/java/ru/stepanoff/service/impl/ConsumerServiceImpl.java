package ru.stepanoff.service.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.stepanoff.client.ManagerClient;
import ru.stepanoff.client.MetricsClient;
import ru.stepanoff.client.ProcessorClient;
import ru.stepanoff.constants.MessagesEnum;
import ru.stepanoff.dto.ConsumerRuleDTO;
import ru.stepanoff.dto.ProcessorDTO;
import ru.stepanoff.handler.ConsumerExceptionHandler;
import ru.stepanoff.helper.JsonHelper;
import ru.stepanoff.helper.exception.JsonHelperException;
import ru.stepanoff.service.ConsumerService;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    /**
     * Класс содержит информацию для рабочих потоков сервиса (которые вычитывают данные из Кафки пользователя)
     */
    @AllArgsConstructor
    private static class ConsumerWorkersThreadsInfo {
        /**
         * Правило для работы текущего сервиса (потребителя)
         */
        public ConsumerRuleDTO consumerRuleDTO;

        /**
         * Список KafkaConsumers для потоков. Для каждого потока свой KafkaConsumer
         */
        public List<KafkaConsumer<String, String>> kafkaConsumers;
    }

    private final AtomicReference<Optional<ConsumerWorkersThreadsInfo>> optionalConsumerWorkersThreadsInfoAtomicRef = new AtomicReference<>(Optional.empty());

    private final JsonHelper jsonHelper;

    private final ProcessorClient processorClient;

    private final MetricsClient metricsClient;

    private final ConsumerExceptionHandler consumerExceptionHandler;

    private final ManagerClient managerClient;

    private ExecutorService executorForConsumers = Executors.newFixedThreadPool(1);
    private final List<Future<Void>> consumerWorkerFutures = new ArrayList<>();
    private boolean needConsuming = false;


    @EventListener({ContextRefreshedEvent.class})
    private void init() {
        managerClient.send(MessagesEnum.RULE_REPEAT.message);
    }

    @Override
    public void updateConsumerRule(ConsumerRuleDTO newConsumerRule) {
        if (!needToUpdateRule(newConsumerRule)) {
            return;
        }

        stopConsuming();
        updateConsumerWorkersThreadInfo(newConsumerRule);
        startConsuming(newConsumerRule.getNumberOfConsumerThreads());
    }

    private void stopConsuming() {
        needConsuming = false;

        log.debug("Останавливаю Кафку");
        try {
            for (Future<Void> consumerWorkerFuture : consumerWorkerFutures) {
                consumerWorkerFuture.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Не смог остановить потребителя, ошибка: " + e.getMessage());
        }
        executorForConsumers.shutdown();
        consumerWorkerFutures.clear();
    }

    private boolean needToUpdateRule(ConsumerRuleDTO newConsumerRule) {
        var optionalConsumerWorkersThreadsInfo = optionalConsumerWorkersThreadsInfoAtomicRef.get();
        if (optionalConsumerWorkersThreadsInfo.isEmpty()) {
            return true;
        }

        ConsumerRuleDTO currentConsumerRule = optionalConsumerWorkersThreadsInfo.get().consumerRuleDTO;
        if (!currentConsumerRule.isReadyToStartConsuming()) {
            return true;
        }
        if (currentConsumerRule.equals(newConsumerRule)) {
            return false;
        }
        return newConsumerRule.getCreatedAt() > currentConsumerRule.getCreatedAt();
    }

    private void updateConsumerWorkersThreadInfo(ConsumerRuleDTO newConsumerRule) {
        List<KafkaConsumer<String, String>> newKafkaConsumers = IntStream
                .range(0, newConsumerRule.getNumberOfConsumerThreads())
                .mapToObj(i -> {
                    KafkaConsumer<String, String> newKafkaConsumer = new KafkaConsumer<>(
                            Map.of(
                                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, newConsumerRule.getKafkaBootstrapServices(),
                                    ConsumerConfig.GROUP_ID_CONFIG, newConsumerRule.getGroupId(),
                                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, newConsumerRule.getKafkaAutoOffsetReset().toLowerCase(),
                                    ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, newConsumerRule.getRequestTimeoutMs()
                            ),
                            new StringDeserializer(),
                            new StringDeserializer()
                    );
                    newKafkaConsumer.subscribe(
                            newConsumerRule.getTopicAliasAndItsInfo()
                                    .values()
                                    .stream()
                                    .flatMap(topicInfo -> topicInfo.getTopicNames().stream())
                                    .collect(Collectors.toSet())
                    );
                    return newKafkaConsumer;
                })
                .toList();

        var optionalOldConsumerWorkersThreadInfo = optionalConsumerWorkersThreadsInfoAtomicRef.get();
        ConsumerWorkersThreadsInfo newConsumerWorkersThreadsInfo = new ConsumerWorkersThreadsInfo(newConsumerRule, newKafkaConsumers);
        optionalConsumerWorkersThreadsInfoAtomicRef.set(Optional.of(newConsumerWorkersThreadsInfo));
        optionalOldConsumerWorkersThreadInfo.ifPresent(oldConsumerWorkersThreadsInfo -> oldConsumerWorkersThreadsInfo.kafkaConsumers.forEach(KafkaConsumer::close));
    }

    private void startConsuming(int numberOfConsumingThreads) {
        needConsuming = true;

        log.debug("Начинаю читать данные из Кафки");
        executorForConsumers = Executors.newFixedThreadPool(numberOfConsumingThreads);
        for (int i = 0; i < numberOfConsumingThreads; i++) {
            int finalI = i;
            Future<Void> future = executorForConsumers.submit(() -> {
                while (needConsuming) {
                    try {
                        while (needConsuming) {
                            Optional<ConsumerWorkersThreadsInfo> optionalConsumerWorkersThreadInfo = optionalConsumerWorkersThreadsInfoAtomicRef.get();
                            if (optionalConsumerWorkersThreadInfo.isEmpty()) {
                                log.warn("Не могу обработать сообщение, так как нет правила работы потребителя");
                                continue;
                            }

                            ConsumerWorkersThreadsInfo consumerWorkersThreadsInfo = optionalConsumerWorkersThreadInfo.get();
                            KafkaConsumer<String, String> kafkaConsumer = consumerWorkersThreadsInfo.kafkaConsumers.get(finalI);
                            ConsumerRuleDTO consumerRuleToProcessRecord = consumerWorkersThreadsInfo.consumerRuleDTO;

                            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
                            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                                log.debug("Получил сообщение из топика {} от пользователя: {}", consumerRecord.topic(), consumerRecord.value());
                                processConsumerKafkaRecord(consumerRecord, consumerRuleToProcessRecord);
                            }
                        }
                    } catch (Exception e) {
                        consumerExceptionHandler.handle(e);
                    }
                }
                return null;
            });
            consumerWorkerFutures.add(future);
        }
    }

    /**
     * @param consumerRecord сообщение из Кафки от пользователя
     * @param consumerRule   правило, по которому сообщение из Кафки должно быть обработано
     * @throws JsonHelperException если строка-json в consumerRecord не соотвествует правилу consumerRule
     */
    private void processConsumerKafkaRecord(ConsumerRecord<String, String> consumerRecord, ConsumerRuleDTO consumerRule) throws JsonHelperException {
        metricsClient.sendKafkaRecordInfo(consumerRecord);

        String topicAlias = getTopicAliasForTopicName(consumerRecord.topic(), consumerRule);
        String value = consumerRecord.value();

        ConsumerRuleDTO.TopicInfo infoForTopic = consumerRule.getTopicAliasAndItsInfo().get(topicAlias);
        if (infoForTopic == null) {
            throw new IllegalArgumentException("Неизвестный топик '%s'".formatted(topicAlias));
        }

        Set<String> fieldsToTake = infoForTopic.getFieldsToTake();
        Map<String, String> fieldsAndValuesToSend = jsonHelper.getFieldsWithValuesFromJson(value, fieldsToTake);

        ProcessorDTO processorDTO = new ProcessorDTO();
        processorDTO.setTopicAlias(topicAlias);
        processorDTO.setFieldNameAndItsValue(fieldsAndValuesToSend);
        log.debug("Хочу отправить процессору сообщение: {}", processorDTO);
        processorClient
                .sendToProcessorAsync(processorDTO)
                .onErrorResume(throwable -> {
                    consumerExceptionHandler.handle(throwable);
                    metricsClient.incrementNumberOfErrors();
                    return Mono.empty();
                })
                .subscribe(response -> {
                    log.debug("Отправил процессору сообщение: {}. Вот ответ: {}", processorDTO, response);
                    metricsClient.incrementNumberOfSentMessagesToProcessor();
                });
    }

    /**
     * @param topicName    - название топика в Кафке
     * @param consumerRule - текущее правило работы потребителя
     * @return для конкретного названия топика в Кафке его псевдоним (alias)
     */
    private String getTopicAliasForTopicName(String topicName, ConsumerRuleDTO consumerRule) {
        for (String topicAlias : consumerRule.getTopicAliasAndItsInfo().keySet()) {
            ConsumerRuleDTO.TopicInfo topicInfo = consumerRule.getTopicAliasAndItsInfo().get(topicAlias);
            if (topicInfo.getTopicNames().stream().anyMatch(kafkaTopicName -> kafkaTopicName.equals(topicName))) {
                return topicAlias;
            }
        }
        throw new IllegalStateException("Неизвестный топик %s".formatted(topicName));
    }
}
