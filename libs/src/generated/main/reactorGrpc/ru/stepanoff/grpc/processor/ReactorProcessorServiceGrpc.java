package ru.stepanoff.grpc.processor;

import static ru.stepanoff.grpc.processor.ProcessorServiceGrpc.getServiceDescriptor;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;


@javax.annotation.Generated(
value = "by ReactorGrpc generator",
comments = "Source: processor.proto")
public final class ReactorProcessorServiceGrpc {
    private ReactorProcessorServiceGrpc() {}

    public static ReactorProcessorServiceStub newReactorStub(io.grpc.Channel channel) {
        return new ReactorProcessorServiceStub(channel);
    }

    public static final class ReactorProcessorServiceStub extends io.grpc.stub.AbstractStub<ReactorProcessorServiceStub> {
        private ProcessorServiceGrpc.ProcessorServiceStub delegateStub;

        private ReactorProcessorServiceStub(io.grpc.Channel channel) {
            super(channel);
            delegateStub = ProcessorServiceGrpc.newStub(channel);
        }

        private ReactorProcessorServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
            delegateStub = ProcessorServiceGrpc.newStub(channel).build(channel, callOptions);
        }

        @Override
        protected ReactorProcessorServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new ReactorProcessorServiceStub(channel, callOptions);
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.EmptyProcessorResponse> sendToProducer(reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.ProcessorDTO> reactorRequest) {
            return com.salesforce.reactorgrpc.stub.ClientCalls.oneToOne(reactorRequest, delegateStub::sendToProducer, getCallOptions());
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.EmptyProcessorResponse> echo(reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.EmptyProcessorRequest> reactorRequest) {
            return com.salesforce.reactorgrpc.stub.ClientCalls.oneToOne(reactorRequest, delegateStub::echo, getCallOptions());
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.EmptyProcessorResponse> sendToProducer(ru.stepanoff.grpc.processor.ProcessorDTO reactorRequest) {
           return com.salesforce.reactorgrpc.stub.ClientCalls.oneToOne(reactor.core.publisher.Mono.just(reactorRequest), delegateStub::sendToProducer, getCallOptions());
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.EmptyProcessorResponse> echo(ru.stepanoff.grpc.processor.EmptyProcessorRequest reactorRequest) {
           return com.salesforce.reactorgrpc.stub.ClientCalls.oneToOne(reactor.core.publisher.Mono.just(reactorRequest), delegateStub::echo, getCallOptions());
        }

    }

    public static abstract class ProcessorServiceImplBase implements io.grpc.BindableService {

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.EmptyProcessorResponse> sendToProducer(ru.stepanoff.grpc.processor.ProcessorDTO request) {
            return sendToProducer(reactor.core.publisher.Mono.just(request));
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.EmptyProcessorResponse> sendToProducer(reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.ProcessorDTO> request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.EmptyProcessorResponse> echo(ru.stepanoff.grpc.processor.EmptyProcessorRequest request) {
            return echo(reactor.core.publisher.Mono.just(request));
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.EmptyProcessorResponse> echo(reactor.core.publisher.Mono<ru.stepanoff.grpc.processor.EmptyProcessorRequest> request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            ru.stepanoff.grpc.processor.ProcessorServiceGrpc.getSendToProducerMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            ru.stepanoff.grpc.processor.ProcessorDTO,
                                            ru.stepanoff.grpc.processor.EmptyProcessorResponse>(
                                            this, METHODID_SEND_TO_PRODUCER)))
                    .addMethod(
                            ru.stepanoff.grpc.processor.ProcessorServiceGrpc.getEchoMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            ru.stepanoff.grpc.processor.EmptyProcessorRequest,
                                            ru.stepanoff.grpc.processor.EmptyProcessorResponse>(
                                            this, METHODID_ECHO)))
                    .build();
        }

        protected io.grpc.CallOptions getCallOptions(int methodId) {
            return null;
        }

        protected Throwable onErrorMap(Throwable throwable) {
            return com.salesforce.reactorgrpc.stub.ServerCalls.prepareError(throwable);
        }
    }

    public static final int METHODID_SEND_TO_PRODUCER = 0;
    public static final int METHODID_ECHO = 1;

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
        private final ProcessorServiceImplBase serviceImpl;
        private final int methodId;

        MethodHandlers(ProcessorServiceImplBase serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_SEND_TO_PRODUCER:
                    com.salesforce.reactorgrpc.stub.ServerCalls.oneToOne((ru.stepanoff.grpc.processor.ProcessorDTO) request,
                            (io.grpc.stub.StreamObserver<ru.stepanoff.grpc.processor.EmptyProcessorResponse>) responseObserver,
                            serviceImpl::sendToProducer, serviceImpl::onErrorMap);
                    break;
                case METHODID_ECHO:
                    com.salesforce.reactorgrpc.stub.ServerCalls.oneToOne((ru.stepanoff.grpc.processor.EmptyProcessorRequest) request,
                            (io.grpc.stub.StreamObserver<ru.stepanoff.grpc.processor.EmptyProcessorResponse>) responseObserver,
                            serviceImpl::echo, serviceImpl::onErrorMap);
                    break;
                default:
                    throw new java.lang.AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                default:
                    throw new java.lang.AssertionError();
            }
        }
    }

}
