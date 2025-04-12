package ru.stepanoff.client;

import ru.stepanoff.dto.ProcessorRuleDTO;

public interface ProcessorClient {
    void sendRule(ProcessorRuleDTO processorRuleDTO);
}
