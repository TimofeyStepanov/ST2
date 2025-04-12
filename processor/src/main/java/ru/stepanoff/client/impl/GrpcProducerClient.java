package ru.stepanoff.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.stepanoff.client.ProducerClient;
import ru.stepanoff.dto.ProducerDTO;
import ru.stepanoff.dto.ProducerResponse;
import ru.stepanoff.grpc.producer.ReactorProducerServiceGrpc;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcProducerClient implements ProducerClient {

    private final ReactorProducerServiceGrpc.ReactorProducerServiceStub producerServiceStub;

    @Override
    public Mono<ProducerResponse> sendToProducerAsync(ProducerDTO producerDTO) {
        ru.stepanoff.grpc.producer.ProducerDTO grpcProducerDTO = ru.stepanoff.grpc.producer.ProducerDTO
                .newBuilder()
                .addAllMessagesForUser(producerDTO.getMessages())
                .build();

        log.debug("Отправялю продюсеру: {}", grpcProducerDTO);
        return producerServiceStub
                .sendToUser(grpcProducerDTO)
                .map(emptyProducerResponse -> new ProducerResponse());
    }
}
