package ru.stepanoff.grpc.processor;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.52.1)",
    comments = "Source: processor.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ProcessorServiceGrpc {

  private ProcessorServiceGrpc() {}

  public static final String SERVICE_NAME = "ProcessorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ru.stepanoff.grpc.processor.ProcessorDTO,
      ru.stepanoff.grpc.processor.EmptyProcessorResponse> getSendToProducerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "sendToProducer",
      requestType = ru.stepanoff.grpc.processor.ProcessorDTO.class,
      responseType = ru.stepanoff.grpc.processor.EmptyProcessorResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ru.stepanoff.grpc.processor.ProcessorDTO,
      ru.stepanoff.grpc.processor.EmptyProcessorResponse> getSendToProducerMethod() {
    io.grpc.MethodDescriptor<ru.stepanoff.grpc.processor.ProcessorDTO, ru.stepanoff.grpc.processor.EmptyProcessorResponse> getSendToProducerMethod;
    if ((getSendToProducerMethod = ProcessorServiceGrpc.getSendToProducerMethod) == null) {
      synchronized (ProcessorServiceGrpc.class) {
        if ((getSendToProducerMethod = ProcessorServiceGrpc.getSendToProducerMethod) == null) {
          ProcessorServiceGrpc.getSendToProducerMethod = getSendToProducerMethod =
              io.grpc.MethodDescriptor.<ru.stepanoff.grpc.processor.ProcessorDTO, ru.stepanoff.grpc.processor.EmptyProcessorResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "sendToProducer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ru.stepanoff.grpc.processor.ProcessorDTO.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ru.stepanoff.grpc.processor.EmptyProcessorResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProcessorServiceMethodDescriptorSupplier("sendToProducer"))
              .build();
        }
      }
    }
    return getSendToProducerMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ru.stepanoff.grpc.processor.EmptyProcessorRequest,
      ru.stepanoff.grpc.processor.EmptyProcessorResponse> getEchoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "echo",
      requestType = ru.stepanoff.grpc.processor.EmptyProcessorRequest.class,
      responseType = ru.stepanoff.grpc.processor.EmptyProcessorResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ru.stepanoff.grpc.processor.EmptyProcessorRequest,
      ru.stepanoff.grpc.processor.EmptyProcessorResponse> getEchoMethod() {
    io.grpc.MethodDescriptor<ru.stepanoff.grpc.processor.EmptyProcessorRequest, ru.stepanoff.grpc.processor.EmptyProcessorResponse> getEchoMethod;
    if ((getEchoMethod = ProcessorServiceGrpc.getEchoMethod) == null) {
      synchronized (ProcessorServiceGrpc.class) {
        if ((getEchoMethod = ProcessorServiceGrpc.getEchoMethod) == null) {
          ProcessorServiceGrpc.getEchoMethod = getEchoMethod =
              io.grpc.MethodDescriptor.<ru.stepanoff.grpc.processor.EmptyProcessorRequest, ru.stepanoff.grpc.processor.EmptyProcessorResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "echo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ru.stepanoff.grpc.processor.EmptyProcessorRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ru.stepanoff.grpc.processor.EmptyProcessorResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProcessorServiceMethodDescriptorSupplier("echo"))
              .build();
        }
      }
    }
    return getEchoMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ProcessorServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProcessorServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProcessorServiceStub>() {
        @java.lang.Override
        public ProcessorServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProcessorServiceStub(channel, callOptions);
        }
      };
    return ProcessorServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ProcessorServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProcessorServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProcessorServiceBlockingStub>() {
        @java.lang.Override
        public ProcessorServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProcessorServiceBlockingStub(channel, callOptions);
        }
      };
    return ProcessorServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ProcessorServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProcessorServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProcessorServiceFutureStub>() {
        @java.lang.Override
        public ProcessorServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProcessorServiceFutureStub(channel, callOptions);
        }
      };
    return ProcessorServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ProcessorServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void sendToProducer(ru.stepanoff.grpc.processor.ProcessorDTO request,
        io.grpc.stub.StreamObserver<ru.stepanoff.grpc.processor.EmptyProcessorResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendToProducerMethod(), responseObserver);
    }

    /**
     */
    public void echo(ru.stepanoff.grpc.processor.EmptyProcessorRequest request,
        io.grpc.stub.StreamObserver<ru.stepanoff.grpc.processor.EmptyProcessorResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getEchoMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendToProducerMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                ru.stepanoff.grpc.processor.ProcessorDTO,
                ru.stepanoff.grpc.processor.EmptyProcessorResponse>(
                  this, METHODID_SEND_TO_PRODUCER)))
          .addMethod(
            getEchoMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                ru.stepanoff.grpc.processor.EmptyProcessorRequest,
                ru.stepanoff.grpc.processor.EmptyProcessorResponse>(
                  this, METHODID_ECHO)))
          .build();
    }
  }

  /**
   */
  public static final class ProcessorServiceStub extends io.grpc.stub.AbstractAsyncStub<ProcessorServiceStub> {
    private ProcessorServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProcessorServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProcessorServiceStub(channel, callOptions);
    }

    /**
     */
    public void sendToProducer(ru.stepanoff.grpc.processor.ProcessorDTO request,
        io.grpc.stub.StreamObserver<ru.stepanoff.grpc.processor.EmptyProcessorResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendToProducerMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void echo(ru.stepanoff.grpc.processor.EmptyProcessorRequest request,
        io.grpc.stub.StreamObserver<ru.stepanoff.grpc.processor.EmptyProcessorResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getEchoMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ProcessorServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<ProcessorServiceBlockingStub> {
    private ProcessorServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProcessorServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProcessorServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public ru.stepanoff.grpc.processor.EmptyProcessorResponse sendToProducer(ru.stepanoff.grpc.processor.ProcessorDTO request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendToProducerMethod(), getCallOptions(), request);
    }

    /**
     */
    public ru.stepanoff.grpc.processor.EmptyProcessorResponse echo(ru.stepanoff.grpc.processor.EmptyProcessorRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getEchoMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ProcessorServiceFutureStub extends io.grpc.stub.AbstractFutureStub<ProcessorServiceFutureStub> {
    private ProcessorServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProcessorServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProcessorServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ru.stepanoff.grpc.processor.EmptyProcessorResponse> sendToProducer(
        ru.stepanoff.grpc.processor.ProcessorDTO request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendToProducerMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ru.stepanoff.grpc.processor.EmptyProcessorResponse> echo(
        ru.stepanoff.grpc.processor.EmptyProcessorRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getEchoMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_TO_PRODUCER = 0;
  private static final int METHODID_ECHO = 1;

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
          serviceImpl.sendToProducer((ru.stepanoff.grpc.processor.ProcessorDTO) request,
              (io.grpc.stub.StreamObserver<ru.stepanoff.grpc.processor.EmptyProcessorResponse>) responseObserver);
          break;
        case METHODID_ECHO:
          serviceImpl.echo((ru.stepanoff.grpc.processor.EmptyProcessorRequest) request,
              (io.grpc.stub.StreamObserver<ru.stepanoff.grpc.processor.EmptyProcessorResponse>) responseObserver);
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

  private static abstract class ProcessorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ProcessorServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ru.stepanoff.grpc.processor.ProcessorProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ProcessorService");
    }
  }

  private static final class ProcessorServiceFileDescriptorSupplier
      extends ProcessorServiceBaseDescriptorSupplier {
    ProcessorServiceFileDescriptorSupplier() {}
  }

  private static final class ProcessorServiceMethodDescriptorSupplier
      extends ProcessorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ProcessorServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ProcessorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ProcessorServiceFileDescriptorSupplier())
              .addMethod(getSendToProducerMethod())
              .addMethod(getEchoMethod())
              .build();
        }
      }
    }
    return result;
  }
}
