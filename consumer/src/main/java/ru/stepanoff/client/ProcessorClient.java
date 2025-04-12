package ru.stepanoff.client;

import reactor.core.publisher.Mono;
import ru.stepanoff.dto.ProcessorDTO;
import ru.stepanoff.dto.ProcessorResponse;

public interface ProcessorClient {
     Mono<ProcessorResponse> sendToProcessorAsync(ProcessorDTO processorDTO);
}
