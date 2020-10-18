package com.github.rajasekharb.grpc.practice.server;


import com.github.rajasekharb.grpc.practice.service.CalculatorServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        //Create a server instance on port 50051 and add the CalculatorServiceImpl service
        // which extends the CalculatorServiceGrpc.CalculatorServiceImplBase
        Server server = ServerBuilder.forPort(50051).addService(new CalculatorServiceImpl()).build();
        server.start();

        //Add a shutdown hook to the server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Retrieved shutdown request");
            server.shutdown();
            System.out.println("Server shutdown successfully");
        }));

        //Server awaits for termination request
        server.awaitTermination();
    }
}
