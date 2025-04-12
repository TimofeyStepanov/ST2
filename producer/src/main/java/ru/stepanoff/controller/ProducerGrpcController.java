package ru.stepanoff.controller;

import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import ru.stepanoff.grpc.producer.EmptyProducerRequest;
import ru.stepanoff.grpc.producer.EmptyProducerResponse;
import ru.stepanoff.grpc.producer.ProducerDTO;
import ru.stepanoff.grpc.producer.ReactorProducerServiceGrpc;
import ru.stepanoff.service.ProducerService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ProducerGrpcController extends ReactorProducerServiceGrpc.ProducerServiceImplBase {

    private final ProducerService producerService;

    @Override
    public Mono<EmptyProducerResponse> echo(Mono<EmptyProducerRequest> monoRequest) {
        return monoRequest.map(r -> EmptyProducerResponse.getDefaultInstance());
    }

    @Override
    public Mono<EmptyProducerResponse> sendToUser(Mono<ProducerDTO> monoRequest) {
        return monoRequest
                .doOnNext(request -> {
                    log.debug("Сообщение от процеcсора: {}", request);
                    producerService.sendToUser(request.getMessagesForUserList());
                })
                .map(request -> EmptyProducerResponse.getDefaultInstance())
                .doOnError(throwable -> log.error("Ошибка: {}", throwable.getMessage()))
                .onErrorResume(throwable -> Mono.error(Status.INVALID_ARGUMENT.withDescription("Ошибка при отправке сообщения: " + throwable.getMessage()).asRuntimeException()));
    }
}
