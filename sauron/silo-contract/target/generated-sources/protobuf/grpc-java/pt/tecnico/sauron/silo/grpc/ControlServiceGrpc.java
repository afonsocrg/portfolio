package pt.tecnico.sauron.silo.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.28.0)",
    comments = "Source: silo.proto")
public final class ControlServiceGrpc {

  private ControlServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.tecnico.sauron.silo.grpc.ControlService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.PingRequest,
      pt.tecnico.sauron.silo.grpc.Silo.PingResponse> getPingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Ping",
      requestType = pt.tecnico.sauron.silo.grpc.Silo.PingRequest.class,
      responseType = pt.tecnico.sauron.silo.grpc.Silo.PingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.PingRequest,
      pt.tecnico.sauron.silo.grpc.Silo.PingResponse> getPingMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.PingRequest, pt.tecnico.sauron.silo.grpc.Silo.PingResponse> getPingMethod;
    if ((getPingMethod = ControlServiceGrpc.getPingMethod) == null) {
      synchronized (ControlServiceGrpc.class) {
        if ((getPingMethod = ControlServiceGrpc.getPingMethod) == null) {
          ControlServiceGrpc.getPingMethod = getPingMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.sauron.silo.grpc.Silo.PingRequest, pt.tecnico.sauron.silo.grpc.Silo.PingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.PingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.PingResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ControlServiceMethodDescriptorSupplier("Ping"))
              .build();
        }
      }
    }
    return getPingMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.ClearRequest,
      pt.tecnico.sauron.silo.grpc.Silo.ClearResponse> getClearMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Clear",
      requestType = pt.tecnico.sauron.silo.grpc.Silo.ClearRequest.class,
      responseType = pt.tecnico.sauron.silo.grpc.Silo.ClearResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.ClearRequest,
      pt.tecnico.sauron.silo.grpc.Silo.ClearResponse> getClearMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.ClearRequest, pt.tecnico.sauron.silo.grpc.Silo.ClearResponse> getClearMethod;
    if ((getClearMethod = ControlServiceGrpc.getClearMethod) == null) {
      synchronized (ControlServiceGrpc.class) {
        if ((getClearMethod = ControlServiceGrpc.getClearMethod) == null) {
          ControlServiceGrpc.getClearMethod = getClearMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.sauron.silo.grpc.Silo.ClearRequest, pt.tecnico.sauron.silo.grpc.Silo.ClearResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Clear"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.ClearRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.ClearResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ControlServiceMethodDescriptorSupplier("Clear"))
              .build();
        }
      }
    }
    return getClearMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest,
      pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse> getInitCamsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InitCams",
      requestType = pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest.class,
      responseType = pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest,
      pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse> getInitCamsMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest, pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse> getInitCamsMethod;
    if ((getInitCamsMethod = ControlServiceGrpc.getInitCamsMethod) == null) {
      synchronized (ControlServiceGrpc.class) {
        if ((getInitCamsMethod = ControlServiceGrpc.getInitCamsMethod) == null) {
          ControlServiceGrpc.getInitCamsMethod = getInitCamsMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest, pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "InitCams"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ControlServiceMethodDescriptorSupplier("InitCams"))
              .build();
        }
      }
    }
    return getInitCamsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest,
      pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse> getInitObservationsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InitObservations",
      requestType = pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest.class,
      responseType = pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest,
      pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse> getInitObservationsMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest, pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse> getInitObservationsMethod;
    if ((getInitObservationsMethod = ControlServiceGrpc.getInitObservationsMethod) == null) {
      synchronized (ControlServiceGrpc.class) {
        if ((getInitObservationsMethod = ControlServiceGrpc.getInitObservationsMethod) == null) {
          ControlServiceGrpc.getInitObservationsMethod = getInitObservationsMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest, pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "InitObservations"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ControlServiceMethodDescriptorSupplier("InitObservations"))
              .build();
        }
      }
    }
    return getInitObservationsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ControlServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ControlServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ControlServiceStub>() {
        @java.lang.Override
        public ControlServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ControlServiceStub(channel, callOptions);
        }
      };
    return ControlServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ControlServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ControlServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ControlServiceBlockingStub>() {
        @java.lang.Override
        public ControlServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ControlServiceBlockingStub(channel, callOptions);
        }
      };
    return ControlServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ControlServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ControlServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ControlServiceFutureStub>() {
        @java.lang.Override
        public ControlServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ControlServiceFutureStub(channel, callOptions);
        }
      };
    return ControlServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ControlServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void ping(pt.tecnico.sauron.silo.grpc.Silo.PingRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.PingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }

    /**
     */
    public void clear(pt.tecnico.sauron.silo.grpc.Silo.ClearRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.ClearResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getClearMethod(), responseObserver);
    }

    /**
     */
    public void initCams(pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getInitCamsMethod(), responseObserver);
    }

    /**
     */
    public void initObservations(pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getInitObservationsMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPingMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.sauron.silo.grpc.Silo.PingRequest,
                pt.tecnico.sauron.silo.grpc.Silo.PingResponse>(
                  this, METHODID_PING)))
          .addMethod(
            getClearMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.sauron.silo.grpc.Silo.ClearRequest,
                pt.tecnico.sauron.silo.grpc.Silo.ClearResponse>(
                  this, METHODID_CLEAR)))
          .addMethod(
            getInitCamsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest,
                pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse>(
                  this, METHODID_INIT_CAMS)))
          .addMethod(
            getInitObservationsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest,
                pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse>(
                  this, METHODID_INIT_OBSERVATIONS)))
          .build();
    }
  }

  /**
   */
  public static final class ControlServiceStub extends io.grpc.stub.AbstractAsyncStub<ControlServiceStub> {
    private ControlServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ControlServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ControlServiceStub(channel, callOptions);
    }

    /**
     */
    public void ping(pt.tecnico.sauron.silo.grpc.Silo.PingRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.PingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void clear(pt.tecnico.sauron.silo.grpc.Silo.ClearRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.ClearResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getClearMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void initCams(pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInitCamsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void initObservations(pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInitObservationsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ControlServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<ControlServiceBlockingStub> {
    private ControlServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ControlServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ControlServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.tecnico.sauron.silo.grpc.Silo.PingResponse ping(pt.tecnico.sauron.silo.grpc.Silo.PingRequest request) {
      return blockingUnaryCall(
          getChannel(), getPingMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.sauron.silo.grpc.Silo.ClearResponse clear(pt.tecnico.sauron.silo.grpc.Silo.ClearRequest request) {
      return blockingUnaryCall(
          getChannel(), getClearMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse initCams(pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest request) {
      return blockingUnaryCall(
          getChannel(), getInitCamsMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse initObservations(pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest request) {
      return blockingUnaryCall(
          getChannel(), getInitObservationsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ControlServiceFutureStub extends io.grpc.stub.AbstractFutureStub<ControlServiceFutureStub> {
    private ControlServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ControlServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ControlServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.sauron.silo.grpc.Silo.PingResponse> ping(
        pt.tecnico.sauron.silo.grpc.Silo.PingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.sauron.silo.grpc.Silo.ClearResponse> clear(
        pt.tecnico.sauron.silo.grpc.Silo.ClearRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getClearMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse> initCams(
        pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInitCamsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse> initObservations(
        pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInitObservationsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PING = 0;
  private static final int METHODID_CLEAR = 1;
  private static final int METHODID_INIT_CAMS = 2;
  private static final int METHODID_INIT_OBSERVATIONS = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ControlServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ControlServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PING:
          serviceImpl.ping((pt.tecnico.sauron.silo.grpc.Silo.PingRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.PingResponse>) responseObserver);
          break;
        case METHODID_CLEAR:
          serviceImpl.clear((pt.tecnico.sauron.silo.grpc.Silo.ClearRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.ClearResponse>) responseObserver);
          break;
        case METHODID_INIT_CAMS:
          serviceImpl.initCams((pt.tecnico.sauron.silo.grpc.Silo.InitCamsRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.InitCamsResponse>) responseObserver);
          break;
        case METHODID_INIT_OBSERVATIONS:
          serviceImpl.initObservations((pt.tecnico.sauron.silo.grpc.Silo.InitObservationsRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.InitObservationsResponse>) responseObserver);
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

  private static abstract class ControlServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ControlServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.tecnico.sauron.silo.grpc.Silo.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ControlService");
    }
  }

  private static final class ControlServiceFileDescriptorSupplier
      extends ControlServiceBaseDescriptorSupplier {
    ControlServiceFileDescriptorSupplier() {}
  }

  private static final class ControlServiceMethodDescriptorSupplier
      extends ControlServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ControlServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ControlServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ControlServiceFileDescriptorSupplier())
              .addMethod(getPingMethod())
              .addMethod(getClearMethod())
              .addMethod(getInitCamsMethod())
              .addMethod(getInitObservationsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
