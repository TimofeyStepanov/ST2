package ru.stepanoff.service;

import reactor.core.publisher.Mono;
import ru.stepanoff.dto.ProcessorDTO;
import ru.stepanoff.dto.ProcessorResponse;
import ru.stepanoff.dto.ProcessorRuleDTO;

public interface ProcessorService {
    Mono<ProcessorResponse> process(ProcessorDTO processorDTO);

    void updateProcessorRule(ProcessorRuleDTO processorRule);
}
