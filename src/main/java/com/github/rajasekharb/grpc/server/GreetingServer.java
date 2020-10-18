package com.github.rajasekharb.grpc.server;

import com.github.rajasekharb.grpc.service.GreetingServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC");

        Server server = ServerBuilder.forPort(50051).addService(new GreetingServiceImpl()).build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Server shutdown successfully");
        }));

        server.awaitTermination();
    }
}
