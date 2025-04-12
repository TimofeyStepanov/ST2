package ru.stepanoff.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import ru.stepanoff.grpc.processor.EmptyProcessorRequest;
import ru.stepanoff.grpc.processor.EmptyProcessorResponse;
import ru.stepanoff.grpc.processor.ProcessorDTO;
import ru.stepanoff.grpc.processor.ReactorProcessorServiceGrpc;
import ru.stepanoff.service.ProcessorService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ProcessorGrpcController extends ReactorProcessorServiceGrpc.ProcessorServiceImplBase {

    private final ProcessorService processorService;

    @Override
    public Mono<EmptyProcessorResponse> echo(Mono<EmptyProcessorRequest> monoRequest) {
        return monoRequest.map(request -> EmptyProcessorResponse.getDefaultInstance());
    }

    @Override
    public Mono<EmptyProcessorResponse> sendToProducer(Mono<ProcessorDTO> monoRequest) {
        return monoRequest
                .flatMap(request -> {
                    log.debug("Получил сообщение от потребителя: {}", request);
                    ru.stepanoff.dto.ProcessorDTO processorDTO = new ru.stepanoff.dto.ProcessorDTO();
                    processorDTO.setTopicAlias(request.getTopicAlias());
                    processorDTO.setFieldNameAndItsValue(request.getFieldNameAndItsValueMap());
                    return processorService.process(processorDTO);
                })
                .map(response -> EmptyProcessorResponse.getDefaultInstance())
                .doOnError(throwable -> log.error(throwable.getMessage()));
    }
}
