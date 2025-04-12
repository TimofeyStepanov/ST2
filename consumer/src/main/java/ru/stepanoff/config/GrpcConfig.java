package ru.stepanoff.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.stepanoff.grpc.processor.ReactorProcessorServiceGrpc;

@Configuration
public class GrpcConfig {

    @Bean
    public ManagedChannel getManagedChannel(
            @Value("${processor.host}") String processorAddressName,
            @Value("${processor.port}") Integer port) {
        return ManagedChannelBuilder
                .forAddress(processorAddressName, port)
                .usePlaintext()
                .build();
    }

    @Bean
    public ReactorProcessorServiceGrpc.ReactorProcessorServiceStub createStub(ManagedChannel managedChannel) {
        return ReactorProcessorServiceGrpc.newReactorStub(managedChannel);
    }
}
