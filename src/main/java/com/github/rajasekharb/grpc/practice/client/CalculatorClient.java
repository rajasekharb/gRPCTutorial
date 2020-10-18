package com.github.rajasekharb.grpc.practice.client;

import com.proto.calculator.CalculatorRequest;
import com.proto.calculator.CalculatorResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.Message;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

    public static void main(String[] args) {
        //Create ManagedChannel instance with the host and port details
        //add usePlaintext method so that SSL error won't come
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        //Create a calculator client using the CalculatorServiceGrpc blocking or non blocking stub
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = CalculatorServiceGrpc.newBlockingStub(channel);

        Message message = Message.newBuilder().setFirstNumber(1).setSecondNumber(2).build();
        CalculatorRequest request = CalculatorRequest.newBuilder().setMessage(message).build();

        CalculatorResponse calculatorResponse = calculatorClient.calculate(request);

        System.out.println("Response from the server is " + calculatorResponse.getResponse());
    }
}
