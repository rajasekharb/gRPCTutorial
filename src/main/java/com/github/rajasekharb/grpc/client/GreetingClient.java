package com.github.rajasekharb.grpc.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

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

        //Create a blocking stub (synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(channel);
        //Created a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder().setFirstName("Rajasekhar").setLastName("Bhupasamudram").build();

        //greet(blockingStub, greeting);

        greetManyTimes(blockingStub, greeting);

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    //Server streaming
    private void greetManyTimes(GreetServiceGrpc.GreetServiceBlockingStub blockingStub, Greeting greeting) {
        //Create the greetManyTimesRequest using the greeting object
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder().setGreeting(greeting).build();
        //Deal with the response sent by the server. Server sends the response multiple times.
        blockingStub.greetManyTimes(greetManyTimesRequest).forEachRemaining(greetManyTimesResponse -> {
            System.out.println(greetManyTimesResponse.getResult());
        });
    }

    //Unary
    private void greet(GreetServiceGrpc.GreetServiceBlockingStub blockingStub, Greeting greeting) {
        //Created a protocol buffer greet request
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

        //Call the RPC and get back a response
        GreetResponse greetResponse = blockingStub.greet(greetRequest);
        System.out.println(greetResponse.getResult());
    }
}
