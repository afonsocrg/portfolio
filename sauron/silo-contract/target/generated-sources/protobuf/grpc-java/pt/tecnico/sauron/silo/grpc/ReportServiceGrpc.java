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
public final class ReportServiceGrpc {

  private ReportServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.tecnico.sauron.silo.grpc.ReportService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.JoinRequest,
      pt.tecnico.sauron.silo.grpc.Silo.JoinResponse> getCamJoinMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CamJoin",
      requestType = pt.tecnico.sauron.silo.grpc.Silo.JoinRequest.class,
      responseType = pt.tecnico.sauron.silo.grpc.Silo.JoinResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.JoinRequest,
      pt.tecnico.sauron.silo.grpc.Silo.JoinResponse> getCamJoinMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.JoinRequest, pt.tecnico.sauron.silo.grpc.Silo.JoinResponse> getCamJoinMethod;
    if ((getCamJoinMethod = ReportServiceGrpc.getCamJoinMethod) == null) {
      synchronized (ReportServiceGrpc.class) {
        if ((getCamJoinMethod = ReportServiceGrpc.getCamJoinMethod) == null) {
          ReportServiceGrpc.getCamJoinMethod = getCamJoinMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.sauron.silo.grpc.Silo.JoinRequest, pt.tecnico.sauron.silo.grpc.Silo.JoinResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CamJoin"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.JoinRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.JoinResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ReportServiceMethodDescriptorSupplier("CamJoin"))
              .build();
        }
      }
    }
    return getCamJoinMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.InfoRequest,
      pt.tecnico.sauron.silo.grpc.Silo.InfoResponse> getCamInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CamInfo",
      requestType = pt.tecnico.sauron.silo.grpc.Silo.InfoRequest.class,
      responseType = pt.tecnico.sauron.silo.grpc.Silo.InfoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.InfoRequest,
      pt.tecnico.sauron.silo.grpc.Silo.InfoResponse> getCamInfoMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.InfoRequest, pt.tecnico.sauron.silo.grpc.Silo.InfoResponse> getCamInfoMethod;
    if ((getCamInfoMethod = ReportServiceGrpc.getCamInfoMethod) == null) {
      synchronized (ReportServiceGrpc.class) {
        if ((getCamInfoMethod = ReportServiceGrpc.getCamInfoMethod) == null) {
          ReportServiceGrpc.getCamInfoMethod = getCamInfoMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.sauron.silo.grpc.Silo.InfoRequest, pt.tecnico.sauron.silo.grpc.Silo.InfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CamInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.InfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.InfoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ReportServiceMethodDescriptorSupplier("CamInfo"))
              .build();
        }
      }
    }
    return getCamInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.ReportRequest,
      pt.tecnico.sauron.silo.grpc.Silo.ReportResponse> getReportMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Report",
      requestType = pt.tecnico.sauron.silo.grpc.Silo.ReportRequest.class,
      responseType = pt.tecnico.sauron.silo.grpc.Silo.ReportResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.ReportRequest,
      pt.tecnico.sauron.silo.grpc.Silo.ReportResponse> getReportMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.sauron.silo.grpc.Silo.ReportRequest, pt.tecnico.sauron.silo.grpc.Silo.ReportResponse> getReportMethod;
    if ((getReportMethod = ReportServiceGrpc.getReportMethod) == null) {
      synchronized (ReportServiceGrpc.class) {
        if ((getReportMethod = ReportServiceGrpc.getReportMethod) == null) {
          ReportServiceGrpc.getReportMethod = getReportMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.sauron.silo.grpc.Silo.ReportRequest, pt.tecnico.sauron.silo.grpc.Silo.ReportResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Report"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.ReportRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.sauron.silo.grpc.Silo.ReportResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ReportServiceMethodDescriptorSupplier("Report"))
              .build();
        }
      }
    }
    return getReportMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ReportServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ReportServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ReportServiceStub>() {
        @java.lang.Override
        public ReportServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ReportServiceStub(channel, callOptions);
        }
      };
    return ReportServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ReportServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ReportServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ReportServiceBlockingStub>() {
        @java.lang.Override
        public ReportServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ReportServiceBlockingStub(channel, callOptions);
        }
      };
    return ReportServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ReportServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ReportServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ReportServiceFutureStub>() {
        @java.lang.Override
        public ReportServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ReportServiceFutureStub(channel, callOptions);
        }
      };
    return ReportServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ReportServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void camJoin(pt.tecnico.sauron.silo.grpc.Silo.JoinRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.JoinResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCamJoinMethod(), responseObserver);
    }

    /**
     */
    public void camInfo(pt.tecnico.sauron.silo.grpc.Silo.InfoRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.InfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCamInfoMethod(), responseObserver);
    }

    /**
     */
    public void report(pt.tecnico.sauron.silo.grpc.Silo.ReportRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.ReportResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getReportMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCamJoinMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.sauron.silo.grpc.Silo.JoinRequest,
                pt.tecnico.sauron.silo.grpc.Silo.JoinResponse>(
                  this, METHODID_CAM_JOIN)))
          .addMethod(
            getCamInfoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.sauron.silo.grpc.Silo.InfoRequest,
                pt.tecnico.sauron.silo.grpc.Silo.InfoResponse>(
                  this, METHODID_CAM_INFO)))
          .addMethod(
            getReportMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.sauron.silo.grpc.Silo.ReportRequest,
                pt.tecnico.sauron.silo.grpc.Silo.ReportResponse>(
                  this, METHODID_REPORT)))
          .build();
    }
  }

  /**
   */
  public static final class ReportServiceStub extends io.grpc.stub.AbstractAsyncStub<ReportServiceStub> {
    private ReportServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReportServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ReportServiceStub(channel, callOptions);
    }

    /**
     */
    public void camJoin(pt.tecnico.sauron.silo.grpc.Silo.JoinRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.JoinResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCamJoinMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void camInfo(pt.tecnico.sauron.silo.grpc.Silo.InfoRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.InfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCamInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void report(pt.tecnico.sauron.silo.grpc.Silo.ReportRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.ReportResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getReportMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ReportServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<ReportServiceBlockingStub> {
    private ReportServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReportServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ReportServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.tecnico.sauron.silo.grpc.Silo.JoinResponse camJoin(pt.tecnico.sauron.silo.grpc.Silo.JoinRequest request) {
      return blockingUnaryCall(
          getChannel(), getCamJoinMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.sauron.silo.grpc.Silo.InfoResponse camInfo(pt.tecnico.sauron.silo.grpc.Silo.InfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getCamInfoMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.sauron.silo.grpc.Silo.ReportResponse report(pt.tecnico.sauron.silo.grpc.Silo.ReportRequest request) {
      return blockingUnaryCall(
          getChannel(), getReportMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ReportServiceFutureStub extends io.grpc.stub.AbstractFutureStub<ReportServiceFutureStub> {
    private ReportServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReportServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ReportServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.sauron.silo.grpc.Silo.JoinResponse> camJoin(
        pt.tecnico.sauron.silo.grpc.Silo.JoinRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCamJoinMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.sauron.silo.grpc.Silo.InfoResponse> camInfo(
        pt.tecnico.sauron.silo.grpc.Silo.InfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCamInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.sauron.silo.grpc.Silo.ReportResponse> report(
        pt.tecnico.sauron.silo.grpc.Silo.ReportRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getReportMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CAM_JOIN = 0;
  private static final int METHODID_CAM_INFO = 1;
  private static final int METHODID_REPORT = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ReportServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ReportServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CAM_JOIN:
          serviceImpl.camJoin((pt.tecnico.sauron.silo.grpc.Silo.JoinRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.JoinResponse>) responseObserver);
          break;
        case METHODID_CAM_INFO:
          serviceImpl.camInfo((pt.tecnico.sauron.silo.grpc.Silo.InfoRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.InfoResponse>) responseObserver);
          break;
        case METHODID_REPORT:
          serviceImpl.report((pt.tecnico.sauron.silo.grpc.Silo.ReportRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.sauron.silo.grpc.Silo.ReportResponse>) responseObserver);
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

  private static abstract class ReportServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ReportServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.tecnico.sauron.silo.grpc.Silo.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ReportService");
    }
  }

  private static final class ReportServiceFileDescriptorSupplier
      extends ReportServiceBaseDescriptorSupplier {
    ReportServiceFileDescriptorSupplier() {}
  }

  private static final class ReportServiceMethodDescriptorSupplier
      extends ReportServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ReportServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ReportServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ReportServiceFileDescriptorSupplier())
              .addMethod(getCamJoinMethod())
              .addMethod(getCamInfoMethod())
              .addMethod(getReportMethod())
              .build();
        }
      }
    }
    return result;
  }
}
