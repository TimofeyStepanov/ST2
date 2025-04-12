package ru.stepanoff.client;

import ru.stepanoff.dto.ProducerRuleDTO;

public interface ProducerClient {
    void sendRule(ProducerRuleDTO consumerRuleDTO);
}
