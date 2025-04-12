package ru.stepanoff.grpc.producer;

import static ru.stepanoff.grpc.producer.ProducerServiceGrpc.getServiceDescriptor;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;


@javax.annotation.Generated(
value = "by ReactorGrpc generator",
comments = "Source: producer.proto")
public final class ReactorProducerServiceGrpc {
    private ReactorProducerServiceGrpc() {}

    public static ReactorProducerServiceStub newReactorStub(io.grpc.Channel channel) {
        return new ReactorProducerServiceStub(channel);
    }

    public static final class ReactorProducerServiceStub extends io.grpc.stub.AbstractStub<ReactorProducerServiceStub> {
        private ProducerServiceGrpc.ProducerServiceStub delegateStub;

        private ReactorProducerServiceStub(io.grpc.Channel channel) {
            super(channel);
            delegateStub = ProducerServiceGrpc.newStub(channel);
        }

        private ReactorProducerServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
            delegateStub = ProducerServiceGrpc.newStub(channel).build(channel, callOptions);
        }

        @Override
        protected ReactorProducerServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new ReactorProducerServiceStub(channel, callOptions);
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.EmptyProducerResponse> sendToUser(reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.ProducerDTO> reactorRequest) {
            return com.salesforce.reactorgrpc.stub.ClientCalls.oneToOne(reactorRequest, delegateStub::sendToUser, getCallOptions());
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.EmptyProducerResponse> echo(reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.EmptyProducerRequest> reactorRequest) {
            return com.salesforce.reactorgrpc.stub.ClientCalls.oneToOne(reactorRequest, delegateStub::echo, getCallOptions());
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.EmptyProducerResponse> sendToUser(ru.stepanoff.grpc.producer.ProducerDTO reactorRequest) {
           return com.salesforce.reactorgrpc.stub.ClientCalls.oneToOne(reactor.core.publisher.Mono.just(reactorRequest), delegateStub::sendToUser, getCallOptions());
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.EmptyProducerResponse> echo(ru.stepanoff.grpc.producer.EmptyProducerRequest reactorRequest) {
           return com.salesforce.reactorgrpc.stub.ClientCalls.oneToOne(reactor.core.publisher.Mono.just(reactorRequest), delegateStub::echo, getCallOptions());
        }

    }

    public static abstract class ProducerServiceImplBase implements io.grpc.BindableService {

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.EmptyProducerResponse> sendToUser(ru.stepanoff.grpc.producer.ProducerDTO request) {
            return sendToUser(reactor.core.publisher.Mono.just(request));
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.EmptyProducerResponse> sendToUser(reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.ProducerDTO> request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.EmptyProducerResponse> echo(ru.stepanoff.grpc.producer.EmptyProducerRequest request) {
            return echo(reactor.core.publisher.Mono.just(request));
        }

        public reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.EmptyProducerResponse> echo(reactor.core.publisher.Mono<ru.stepanoff.grpc.producer.EmptyProducerRequest> request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            ru.stepanoff.grpc.producer.ProducerServiceGrpc.getSendToUserMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            ru.stepanoff.grpc.producer.ProducerDTO,
                                            ru.stepanoff.grpc.producer.EmptyProducerResponse>(
                                            this, METHODID_SEND_TO_USER)))
                    .addMethod(
                            ru.stepanoff.grpc.producer.ProducerServiceGrpc.getEchoMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            ru.stepanoff.grpc.producer.EmptyProducerRequest,
                                            ru.stepanoff.grpc.producer.EmptyProducerResponse>(
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

    public static final int METHODID_SEND_TO_USER = 0;
    public static final int METHODID_ECHO = 1;

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
        private final ProducerServiceImplBase serviceImpl;
        private final int methodId;

        MethodHandlers(ProducerServiceImplBase serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_SEND_TO_USER:
                    com.salesforce.reactorgrpc.stub.ServerCalls.oneToOne((ru.stepanoff.grpc.producer.ProducerDTO) request,
                            (io.grpc.stub.StreamObserver<ru.stepanoff.grpc.producer.EmptyProducerResponse>) responseObserver,
                            serviceImpl::sendToUser, serviceImpl::onErrorMap);
                    break;
                case METHODID_ECHO:
                    com.salesforce.reactorgrpc.stub.ServerCalls.oneToOne((ru.stepanoff.grpc.producer.EmptyProducerRequest) request,
                            (io.grpc.stub.StreamObserver<ru.stepanoff.grpc.producer.EmptyProducerResponse>) responseObserver,
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
