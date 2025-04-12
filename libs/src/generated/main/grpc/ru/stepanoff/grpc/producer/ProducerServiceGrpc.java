package ru.stepanoff.grpc.producer;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.52.1)",
    comments = "Source: producer.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ProducerServiceGrpc {

  private ProducerServiceGrpc() {}

  public static final String SERVICE_NAME = "ProducerService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ru.stepanoff.grpc.producer.ProducerDTO,
      ru.stepanoff.grpc.producer.EmptyProducerResponse> getSendToUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "sendToUser",
      requestType = ru.stepanoff.grpc.producer.ProducerDTO.class,
      responseType = ru.stepanoff.grpc.producer.EmptyProducerResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ru.stepanoff.grpc.producer.ProducerDTO,
      ru.stepanoff.grpc.producer.EmptyProducerResponse> getSendToUserMethod() {
    io.grpc.MethodDescriptor<ru.stepanoff.grpc.producer.ProducerDTO, ru.stepanoff.grpc.producer.EmptyProducerResponse> getSendToUserMethod;
    if ((getSendToUserMethod = ProducerServiceGrpc.getSendToUserMethod) == null) {
      synchronized (ProducerServiceGrpc.class) {
        if ((getSendToUserMethod = ProducerServiceGrpc.getSendToUserMethod) == null) {
          ProducerServiceGrpc.getSendToUserMethod = getSendToUserMethod =
              io.grpc.MethodDescriptor.<ru.stepanoff.grpc.producer.ProducerDTO, ru.stepanoff.grpc.producer.EmptyProducerResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "sendToUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ru.stepanoff.grpc.producer.ProducerDTO.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ru.stepanoff.grpc.producer.EmptyProducerResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProducerServiceMethodDescriptorSupplier("sendToUser"))
              .build();
        }
      }
    }
    return getSendToUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ru.stepanoff.grpc.producer.EmptyProducerRequest,
      ru.stepanoff.grpc.producer.EmptyProducerResponse> getEchoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "echo",
      requestType = ru.stepanoff.grpc.producer.EmptyProducerRequest.class,
      responseType = ru.stepanoff.grpc.producer.EmptyProducerResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ru.stepanoff.grpc.producer.EmptyProducerRequest,
      ru.stepanoff.grpc.producer.EmptyProducerResponse> getEchoMethod() {
    io.grpc.MethodDescriptor<ru.stepanoff.grpc.producer.EmptyProducerRequest, ru.stepanoff.grpc.producer.EmptyProducerResponse> getEchoMethod;
    if ((getEchoMethod = ProducerServiceGrpc.getEchoMethod) == null) {
      synchronized (ProducerServiceGrpc.class) {
        if ((getEchoMethod = ProducerServiceGrpc.getEchoMethod) == null) {
          ProducerServiceGrpc.getEchoMethod = getEchoMethod =
              io.grpc.MethodDescriptor.<ru.stepanoff.grpc.producer.EmptyProducerRequest, ru.stepanoff.grpc.producer.EmptyProducerResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "echo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ru.stepanoff.grpc.producer.EmptyProducerRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ru.stepanoff.grpc.producer.EmptyProducerResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProducerServiceMethodDescriptorSupplier("echo"))
              .build();
        }
      }
    }
    return getEchoMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ProducerServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProducerServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProducerServiceStub>() {
        @java.lang.Override
        public ProducerServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProducerServiceStub(channel, callOptions);
        }
      };
    return ProducerServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ProducerServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProducerServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProducerServiceBlockingStub>() {
        @java.lang.Override
        public ProducerServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProducerServiceBlockingStub(channel, callOptions);
        }
      };
    return ProducerServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ProducerServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProducerServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProducerServiceFutureStub>() {
        @java.lang.Override
        public ProducerServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProducerServiceFutureStub(channel, callOptions);
        }
      };
    return ProducerServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ProducerServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void sendToUser(ru.stepanoff.grpc.producer.ProducerDTO request,
        io.grpc.stub.StreamObserver<ru.stepanoff.grpc.producer.EmptyProducerResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendToUserMethod(), responseObserver);
    }

    /**
     */
    public void echo(ru.stepanoff.grpc.producer.EmptyProducerRequest request,
        io.grpc.stub.StreamObserver<ru.stepanoff.grpc.producer.EmptyProducerResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getEchoMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendToUserMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                ru.stepanoff.grpc.producer.ProducerDTO,
                ru.stepanoff.grpc.producer.EmptyProducerResponse>(
                  this, METHODID_SEND_TO_USER)))
          .addMethod(
            getEchoMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                ru.stepanoff.grpc.producer.EmptyProducerRequest,
                ru.stepanoff.grpc.producer.EmptyProducerResponse>(
                  this, METHODID_ECHO)))
          .build();
    }
  }

  /**
   */
  public static final class ProducerServiceStub extends io.grpc.stub.AbstractAsyncStub<ProducerServiceStub> {
    private ProducerServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProducerServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProducerServiceStub(channel, callOptions);
    }

    /**
     */
    public void sendToUser(ru.stepanoff.grpc.producer.ProducerDTO request,
        io.grpc.stub.StreamObserver<ru.stepanoff.grpc.producer.EmptyProducerResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendToUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void echo(ru.stepanoff.grpc.producer.EmptyProducerRequest request,
        io.grpc.stub.StreamObserver<ru.stepanoff.grpc.producer.EmptyProducerResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getEchoMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ProducerServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<ProducerServiceBlockingStub> {
    private ProducerServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProducerServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProducerServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public ru.stepanoff.grpc.producer.EmptyProducerResponse sendToUser(ru.stepanoff.grpc.producer.ProducerDTO request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendToUserMethod(), getCallOptions(), request);
    }

    /**
     */
    public ru.stepanoff.grpc.producer.EmptyProducerResponse echo(ru.stepanoff.grpc.producer.EmptyProducerRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getEchoMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ProducerServiceFutureStub extends io.grpc.stub.AbstractFutureStub<ProducerServiceFutureStub> {
    private ProducerServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProducerServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProducerServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ru.stepanoff.grpc.producer.EmptyProducerResponse> sendToUser(
        ru.stepanoff.grpc.producer.ProducerDTO request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendToUserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ru.stepanoff.grpc.producer.EmptyProducerResponse> echo(
        ru.stepanoff.grpc.producer.EmptyProducerRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getEchoMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_TO_USER = 0;
  private static final int METHODID_ECHO = 1;

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
          serviceImpl.sendToUser((ru.stepanoff.grpc.producer.ProducerDTO) request,
              (io.grpc.stub.StreamObserver<ru.stepanoff.grpc.producer.EmptyProducerResponse>) responseObserver);
          break;
        case METHODID_ECHO:
          serviceImpl.echo((ru.stepanoff.grpc.producer.EmptyProducerRequest) request,
              (io.grpc.stub.StreamObserver<ru.stepanoff.grpc.producer.EmptyProducerResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ProducerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ProducerServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ru.stepanoff.grpc.producer.ProducerProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ProducerService");
    }
  }

  private static final class ProducerServiceFileDescriptorSupplier
      extends ProducerServiceBaseDescriptorSupplier {
    ProducerServiceFileDescriptorSupplier() {}
  }

  private static final class ProducerServiceMethodDescriptorSupplier
      extends ProducerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ProducerServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ProducerServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ProducerServiceFileDescriptorSupplier())
              .addMethod(getSendToUserMethod())
              .addMethod(getEchoMethod())
              .build();
        }
      }
    }
    return result;
  }
}
