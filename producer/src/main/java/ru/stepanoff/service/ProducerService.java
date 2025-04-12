package ru.stepanoff.service;

import ru.stepanoff.dto.ProducerRuleDTO;

import java.util.List;

public interface ProducerService {
    void sendToUser(List<String> messages);

    void updateProducerRule(ProducerRuleDTO newProducerRule);
}
