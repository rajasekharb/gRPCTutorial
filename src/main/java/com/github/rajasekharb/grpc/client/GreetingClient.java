package com.github.rajasekharb.grpc.client;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Hello, I am gRPC client");

        //Old and dummy
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        //DummyServiceGrpc.DummyServiceBlockingStub client = DummyServiceGrpc.newBlockingStub(channel);

        //Created the greet service client - blocking i.e. synchronous
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        //Created a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder().setFirstName("Rajasekhar").setLastName("Bhupasamudram").build();

        //Created a protocol buffer greet request
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

        //Call the RPC and get back a response
        GreetResponse greetResponse = greetClient.greet(greetRequest);

        System.out.println(greetResponse.getResult());

        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
