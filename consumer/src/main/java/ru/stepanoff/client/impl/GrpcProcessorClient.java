package ru.stepanoff.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.stepanoff.client.ProcessorClient;
import ru.stepanoff.dto.ProcessorDTO;
import ru.stepanoff.dto.ProcessorResponse;
import ru.stepanoff.grpc.processor.ReactorProcessorServiceGrpc;

@Component
@RequiredArgsConstructor
public class GrpcProcessorClient implements ProcessorClient {

    private final ReactorProcessorServiceGrpc.ReactorProcessorServiceStub processorServiceStub;

    @Override
    public Mono<ProcessorResponse> sendToProcessorAsync(ProcessorDTO processorDTO) {
        ru.stepanoff.grpc.processor.ProcessorDTO grpcProcessorDTO = ru.stepanoff.grpc.processor.ProcessorDTO
                .newBuilder()
                .setTopicAlias(processorDTO.getTopicAlias())
                .putAllFieldNameAndItsValue(processorDTO.getFieldNameAndItsValue())
                .build();

        return processorServiceStub
                .sendToProducer(grpcProcessorDTO)
                .map(emptyProcessorResponse -> new ProcessorResponse());
    }
}
