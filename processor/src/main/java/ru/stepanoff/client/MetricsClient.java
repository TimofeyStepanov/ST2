package ru.stepanoff.client;

public interface MetricsClient {
    void incrementNumberOfSentMessagesToProducer();

    void sendDDLScriptsInfo(boolean ddlScriptsProcessedCorrectly);
}
