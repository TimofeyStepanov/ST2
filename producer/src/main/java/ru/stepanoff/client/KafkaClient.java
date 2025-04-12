package ru.stepanoff.client;

public interface KafkaClient {
    void sendAsync(String message);
}
