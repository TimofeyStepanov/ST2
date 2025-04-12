package ru.stepanoff.client;

import ru.stepanoff.dto.ConsumerRuleDTO;

public interface ConsumerClient {
    void sendRule(ConsumerRuleDTO consumerRuleDTO);
}
