package com.github.rajasekharb.grpc.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello, I am gRPC client");
        GreetingClient greetingClient = new GreetingClient();
        greetingClient.run();
    }

    public void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        //Created a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder().setFirstName("Rajasekhar").setLastName("Bhupasamudram").build();

        //greet(channel, greeting);
        //greetManyTimes(channel, greeting);

        //longGreet(channel, greeting);
        greetEveryone(channel, greeting);

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    //Bidirectional streaming
    private void greetEveryone(ManagedChannel channel, Greeting greeting) {
        CountDownLatch latch = new CountDownLatch(1);
        GreetServiceGrpc.GreetServiceStub stub = GreetServiceGrpc.newStub(channel);

        StreamObserver<GreetEveryoneRequest> greetEveryoneRequestStreamObserver = stub.greetEveryone(new StreamObserver<>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                //Server sent response comes here
                System.out.println("Response from the server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                //Server sent onCompleted response comes here
                System.out.println("Server said done!");
                latch.countDown();
            }
        });

        //Send GreetEveryoneRequest as many times as you want here
        Arrays.asList("Rajasekhar", "Rajesh", "Phoenix").forEach((name) -> {
            System.out.println("Sending " + name);
            greetEveryoneRequestStreamObserver
                    .onNext(GreetEveryoneRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName(name).build()).build());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        //Send the message to server that you are done sending the data
        greetEveryoneRequestStreamObserver.onCompleted();
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Client streaming
    private void longGreet(ManagedChannel channel, Greeting greeting) {
        CountDownLatch latch = new CountDownLatch(1);

        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        StreamObserver<LongGreetRequest> longGreetRequestStreamObserver = asyncClient.longGreet(new StreamObserver<>() {
            @Override
            public void onNext(LongGreetResponse value) {
                //We get a response from the server
                System.out.println("Received a response from the server");
                System.out.println(value.getResult());
                //onNext will be called only once by the server as this is a client streaming
            }

            @Override
            public void onError(Throwable t) {
                //We get an error from the server
            }

            @Override
            public void onCompleted() {
                //The server is done sending the data
                //onCompleted will be called by the server after onNext
                System.out.println("Server has completed the processing");
                latch.countDown();
            }
        });

        //Streaming message 1
        System.out.println("Sending message 1");
        longGreetRequestStreamObserver.onNext(LongGreetRequest.newBuilder().setGreeting(greeting).build());
        //Streaming message 2
        System.out.println("Sending message 2");
        longGreetRequestStreamObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Rajesh")).build());
        //Streaming message 3
        System.out.println("Sending message 3");
        longGreetRequestStreamObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Phoenix")).build());

        //We tell server that client is done sending the data
        longGreetRequestStreamObserver.onCompleted();
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private GreetServiceGrpc.GreetServiceBlockingStub getGreetServiceBlockingStub(ManagedChannel channel) {
        //Create a blocking stub (synchronous)
        return GreetServiceGrpc.newBlockingStub(channel);
    }

    //Server streaming
    private void greetManyTimes(ManagedChannel channel, Greeting greeting) {
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = getGreetServiceBlockingStub(channel);
        //Create the greetManyTimesRequest using the greeting object
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder().setGreeting(greeting).build();
        //Deal with the response sent by the server. Server sends the response multiple times.
        blockingStub.greetManyTimes(greetManyTimesRequest).forEachRemaining(greetManyTimesResponse -> {
            System.out.println(greetManyTimesResponse.getResult());
        });
    }

    //Unary
    private void greet(ManagedChannel channel, Greeting greeting) {
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = getGreetServiceBlockingStub(channel);
        //Created a protocol buffer greet request
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

        //Call the RPC and get back a response
        GreetResponse greetResponse = blockingStub.greet(greetRequest);
        System.out.println(greetResponse.getResult());
    }
}
