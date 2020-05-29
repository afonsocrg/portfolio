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
public final class QueryServiceGrpc {

  private QueryServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.tecnico.sauron.silo.grpc.QueryService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.TrackRequest,
      pt.tecnico.sauron.silo.grpc.Silo.TrackResponse> getTrackMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Track",
      requestType = pt.tecnico.sauron.silo.grpc.Silo.TrackRequest.class,
      responseType = pt.tecnico.sauron.silo.grpc.Silo.TrackResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.TrackRequest,
      pt.tecnico.sauron.silo.grpc.Silo.TrackResponse> getTrackMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.TrackRequest, pt.tecnico.sauron.silo.grpc.Silo.TrackResponse> getTrackMethod;
    if ((getTrackMethod = QueryServiceGrpc.getTrackMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getTrackMethod = QueryServiceGrpc.getTrackMethod) == null) {
          QueryServiceGrpc.getTrackMethod = getTrackMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.sauron.silo.grpc.Silo.TrackRequest, pt.tecnico.sauron.silo.grpc.Silo.TrackResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Track"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.TrackRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.TrackResponse.getDefaultInstance()))
              .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("Track"))
              .build();
        }
      }
    }
    return getTrackMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest,
      pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse> getTrackMatchMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TrackMatch",
      requestType = pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest.class,
      responseType = pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest,
      pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse> getTrackMatchMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest, pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse> getTrackMatchMethod;
    if ((getTrackMatchMethod = QueryServiceGrpc.getTrackMatchMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getTrackMatchMethod = QueryServiceGrpc.getTrackMatchMethod) == null) {
          QueryServiceGrpc.getTrackMatchMethod = getTrackMatchMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest, pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TrackMatch"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse.getDefaultInstance()))
              .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("TrackMatch"))
              .build();
        }
      }
    }
    return getTrackMatchMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.TraceRequest,
      pt.tecnico.sauron.silo.grpc.Silo.TraceResponse> getTraceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Trace",
      requestType = pt.tecnico.sauron.silo.grpc.Silo.TraceRequest.class,
      responseType = pt.tecnico.sauron.silo.grpc.Silo.TraceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.TraceRequest,
      pt.tecnico.sauron.silo.grpc.Silo.TraceResponse> getTraceMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.TraceRequest, pt.tecnico.sauron.silo.grpc.Silo.TraceResponse> getTraceMethod;
    if ((getTraceMethod = QueryServiceGrpc.getTraceMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getTraceMethod = QueryServiceGrpc.getTraceMethod) == null) {
          QueryServiceGrpc.getTraceMethod = getTraceMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.sauron.silo.grpc.Silo.TraceRequest, pt.tecnico.sauron.silo.grpc.Silo.TraceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Trace"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.TraceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.TraceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("Trace"))
              .build();
        }
      }
    }
    return getTraceMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static QueryServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QueryServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QueryServiceStub>() {
        @java.lang.Override
        public QueryServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QueryServiceStub(channel, callOptions);
        }
      };
    return QueryServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static QueryServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QueryServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QueryServiceBlockingStub>() {
        @java.lang.Override
        public QueryServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QueryServiceBlockingStub(channel, callOptions);
        }
      };
    return QueryServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static QueryServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QueryServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QueryServiceFutureStub>() {
        @java.lang.Override
        public QueryServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QueryServiceFutureStub(channel, callOptions);
        }
      };
    return QueryServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class QueryServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void track(pt.tecnico.sauron.silo.grpc.Silo.TrackRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.TrackResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTrackMethod(), responseObserver);
    }

    /**
     */
    public void trackMatch(pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTrackMatchMethod(), responseObserver);
    }

    /**
     */
    public void trace(pt.tecnico.sauron.silo.grpc.Silo.TraceRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.TraceResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTraceMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getTrackMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.sauron.silo.grpc.Silo.TrackRequest,
                pt.tecnico.sauron.silo.grpc.Silo.TrackResponse>(
                  this, METHODID_TRACK)))
          .addMethod(
            getTrackMatchMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest,
                pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse>(
                  this, METHODID_TRACK_MATCH)))
          .addMethod(
            getTraceMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.sauron.silo.grpc.Silo.TraceRequest,
                pt.tecnico.sauron.silo.grpc.Silo.TraceResponse>(
                  this, METHODID_TRACE)))
          .build();
    }
  }

  /**
   */
  public static final class QueryServiceStub extends io.grpc.stub.AbstractAsyncStub<QueryServiceStub> {
    private QueryServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QueryServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QueryServiceStub(channel, callOptions);
    }

    /**
     */
    public void track(pt.tecnico.sauron.silo.grpc.Silo.TrackRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.TrackResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTrackMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void trackMatch(pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTrackMatchMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void trace(pt.tecnico.sauron.silo.grpc.Silo.TraceRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.TraceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTraceMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class QueryServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<QueryServiceBlockingStub> {
    private QueryServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QueryServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QueryServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.tecnico.sauron.silo.grpc.Silo.TrackResponse track(pt.tecnico.sauron.silo.grpc.Silo.TrackRequest request) {
      return blockingUnaryCall(
          getChannel(), getTrackMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse trackMatch(pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest request) {
      return blockingUnaryCall(
          getChannel(), getTrackMatchMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.sauron.silo.grpc.Silo.TraceResponse trace(pt.tecnico.sauron.silo.grpc.Silo.TraceRequest request) {
      return blockingUnaryCall(
          getChannel(), getTraceMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class QueryServiceFutureStub extends io.grpc.stub.AbstractFutureStub<QueryServiceFutureStub> {
    private QueryServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QueryServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QueryServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.sauron.silo.grpc.Silo.TrackResponse> track(
        pt.tecnico.sauron.silo.grpc.Silo.TrackRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTrackMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse> trackMatch(
        pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTrackMatchMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.sauron.silo.grpc.Silo.TraceResponse> trace(
        pt.tecnico.sauron.silo.grpc.Silo.TraceRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTraceMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_TRACK = 0;
  private static final int METHODID_TRACK_MATCH = 1;
  private static final int METHODID_TRACE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final QueryServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(QueryServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_TRACK:
          serviceImpl.track((pt.tecnico.sauron.silo.grpc.Silo.TrackRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.TrackResponse>) responseObserver);
          break;
        case METHODID_TRACK_MATCH:
          serviceImpl.trackMatch((pt.tecnico.sauron.silo.grpc.Silo.TrackMatchRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.TrackMatchResponse>) responseObserver);
          break;
        case METHODID_TRACE:
          serviceImpl.trace((pt.tecnico.sauron.silo.grpc.Silo.TraceRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.TraceResponse>) responseObserver);
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

  private static abstract class QueryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    QueryServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.tecnico.sauron.silo.grpc.Silo.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("QueryService");
    }
  }

  private static final class QueryServiceFileDescriptorSupplier
      extends QueryServiceBaseDescriptorSupplier {
    QueryServiceFileDescriptorSupplier() {}
  }

  private static final class QueryServiceMethodDescriptorSupplier
      extends QueryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    QueryServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (QueryServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new QueryServiceFileDescriptorSupplier())
              .addMethod(getTrackMethod())
              .addMethod(getTrackMatchMethod())
              .addMethod(getTraceMethod())
              .build();
        }
      }
    }
    return result;
  }
}
