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
    comments = "Source: gossip.proto")
public final class GossipServiceGrpc {

  private GossipServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.tecnico.sauron.silo.grpc.GossipService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest,
      pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse> getGossipMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Gossip",
      requestType = pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest.class,
      responseType = pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest,
      pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse> getGossipMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest, pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse> getGossipMethod;
    if ((getGossipMethod = GossipServiceGrpc.getGossipMethod) == null) {
      synchronized (GossipServiceGrpc.class) {
        if ((getGossipMethod = GossipServiceGrpc.getGossipMethod) == null) {
          GossipServiceGrpc.getGossipMethod = getGossipMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest, pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Gossip"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GossipServiceMethodDescriptorSupplier("Gossip"))
              .build();
        }
      }
    }
    return getGossipMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GossipServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GossipServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GossipServiceStub>() {
        @java.lang.Override
        public GossipServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GossipServiceStub(channel, callOptions);
        }
      };
    return GossipServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GossipServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GossipServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GossipServiceBlockingStub>() {
        @java.lang.Override
        public GossipServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GossipServiceBlockingStub(channel, callOptions);
        }
      };
    return GossipServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GossipServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GossipServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GossipServiceFutureStub>() {
        @java.lang.Override
        public GossipServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GossipServiceFutureStub(channel, callOptions);
        }
      };
    return GossipServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class GossipServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void gossip(pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGossipMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGossipMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest,
                pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse>(
                  this, METHODID_GOSSIP)))
          .build();
    }
  }

  /**
   */
  public static final class GossipServiceStub extends io.grpc.stub.AbstractAsyncStub<GossipServiceStub> {
    private GossipServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GossipServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GossipServiceStub(channel, callOptions);
    }

    /**
     */
    public void gossip(pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGossipMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GossipServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<GossipServiceBlockingStub> {
    private GossipServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GossipServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GossipServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse gossip(pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest request) {
      return blockingUnaryCall(
          getChannel(), getGossipMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GossipServiceFutureStub extends io.grpc.stub.AbstractFutureStub<GossipServiceFutureStub> {
    private GossipServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GossipServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GossipServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse> gossip(
        pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGossipMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GOSSIP = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GossipServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GossipServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GOSSIP:
          serviceImpl.gossip((pt.tecnico.sauron.silo.grpc.Gossip.GossipRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Gossip.GossipResponse>) responseObserver);
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

  private static abstract class GossipServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GossipServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.tecnico.sauron.silo.grpc.Gossip.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GossipService");
    }
  }

  private static final class GossipServiceFileDescriptorSupplier
      extends GossipServiceBaseDescriptorSupplier {
    GossipServiceFileDescriptorSupplier() {}
  }

  private static final class GossipServiceMethodDescriptorSupplier
      extends GossipServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GossipServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (GossipServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GossipServiceFileDescriptorSupplier())
              .addMethod(getGossipMethod())
              .build();
        }
      }
    }
    return result;
  }
}
