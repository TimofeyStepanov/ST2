package ru.stepanoff.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.stepanoff.grpc.producer.ReactorProducerServiceGrpc;

@Configuration
public class GrpcConfig {

    @Bean
    public ManagedChannel getManagedChannel(
            @Value("${producer.host}") String processorAddressName,
            @Value("${producer.port}") Integer port) {
        return ManagedChannelBuilder
                .forAddress(processorAddressName, port)
                .usePlaintext()
                .build();
    }

    @Bean
    public ReactorProducerServiceGrpc.ReactorProducerServiceStub createStub(ManagedChannel managedChannel) {
        return ReactorProducerServiceGrpc.newReactorStub(managedChannel);
    }
}
