package com.pm.billingservice.grpc;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    // este es el metodo que inicializamos en el .proto
    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest, StreamObserver<billing.BillingResponse> responseObserver) {
        // log for dev
        log.info("createBillingAccount request received : {}", billingRequest.toString());

        // Business logic - e.g save to db, perform calculations, etc

        var response = BillingResponse.newBuilder()
                .setAccountId("123456")
                .setStatus("ACTIVE")
                .build();

        //sending response back patient service
        responseObserver.onNext(response);
        // response completed, end cycle
        responseObserver.onCompleted();
    }
}
