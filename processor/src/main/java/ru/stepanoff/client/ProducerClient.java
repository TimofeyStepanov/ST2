package ru.stepanoff.client;

import reactor.core.publisher.Mono;
import ru.stepanoff.dto.ProducerDTO;
import ru.stepanoff.dto.ProducerResponse;

public interface ProducerClient {
    Mono<ProducerResponse> sendToProducerAsync(ProducerDTO producerDTO);
}
