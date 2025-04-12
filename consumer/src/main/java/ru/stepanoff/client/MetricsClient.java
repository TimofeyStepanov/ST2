package ru.stepanoff.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface MetricsClient {
    void incrementNumberOfErrors();

    <K, V> void sendKafkaRecordInfo(ConsumerRecord<K, V> consumerRecord);

    void incrementNumberOfSentMessagesToProcessor();
}
