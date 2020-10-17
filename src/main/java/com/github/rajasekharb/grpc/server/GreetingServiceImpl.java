package com.github.rajasekharb.grpc.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        //Extract the fields we need
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        //Prepare the response
        String result = "Hello " + firstName;
        GreetResponse response = GreetResponse.newBuilder().setResult(result).build();

        //Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
