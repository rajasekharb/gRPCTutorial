package com.github.rajasekharb.grpc.service;

import com.proto.greet.*;
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

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();

        try {
            for (int index = 0; index < 10; index++) {
                String result = "Hello " + firstName + ", response number: " + index;
                GreetManyTimesResponse greetManyTimesResponse = GreetManyTimesResponse.newBuilder().setResult(result).build();

                responseObserver.onNext(greetManyTimesResponse);
                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        return new StreamObserver<>() {
            String result = "";

            //Client streaming messages are received here. One after the other
            @Override
            public void onNext(LongGreetRequest value) {
                result = result + "Hello " + value.getGreeting().getFirstName() + "! ";
            }

            //Client sent error is received here
            @Override
            public void onError(Throwable t) {
                //NOOP
            }

            //When client says I am done, then this method is called
            @Override
            public void onCompleted() {
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(this.result).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(GreetEveryoneRequest value) {
                String result = "Hello " + value.getGreeting().getFirstName() + "!";
                responseObserver.onNext(GreetEveryoneResponse.newBuilder().setResult(result).build());
                System.out.println("-------------Sent a response to the client-------------");
            }

            @Override
            public void onError(Throwable t) {
                //Do nothing
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
