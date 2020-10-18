package com.github.rajasekharb.grpc.practice.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    public static void main(String[] args) {
        CalculatorClient calculatorClient = new CalculatorClient();
        calculatorClient.run();
    }

    public void run() {
        //Create ManagedChannel instance with the host and port details
        //add usePlaintext method so that SSL error won't come
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        //calculateSum(channel);

        //primeFactorization(channel);

        computeAverage(channel);
    }

    private void computeAverage(ManagedChannel channel) {
        CountDownLatch latch = new CountDownLatch(1);

        //Send a request to the server multiple times
        CalculatorServiceGrpc.CalculatorServiceStub calculatorServiceStub = CalculatorServiceGrpc.newStub(channel);

        StreamObserver<ComputeAverageRequest> computeAverageRequestStreamObserver = calculatorServiceStub.computeAverage(new StreamObserver<>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                //Server sent messages come here
                System.out.println("Server sent the response " + value.getResult());
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                //NOOP
            }

            @Override
            public void onCompleted() {
                //Server sent completed messages reach here
                System.out.println("Server completed processing the request");
                latch.countDown();
            }
        });

        for (int index = 1; index <= 10; index++) {
            //Sending stream
            System.out.println("Sending request number " + index);
            computeAverageRequestStreamObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(index).build());
        }

        computeAverageRequestStreamObserver.onCompleted();
        System.out.println("*****************************************************");
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void primeFactorization(ManagedChannel channel) {
        getCalculatorServiceBlockingStub(channel)
                .primeFactorization(PrimeFactorizationRequest.newBuilder().setMessage(PrimeFactorizationMessage.newBuilder().setNumber(120).build()).build())
                .forEachRemaining(primeFactorizationResponse -> System.out.println("Prime factor - " + primeFactorizationResponse.getResponse()));
    }

    private void calculateSum(ManagedChannel channel) {
        //Create a calculator client using the CalculatorServiceGrpc blocking or non blocking stub
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = getCalculatorServiceBlockingStub(channel);
        Message message = Message.newBuilder().setFirstNumber(1).setSecondNumber(2).build();
        CalculatorRequest request = CalculatorRequest.newBuilder().setMessage(message).build();

        CalculatorResponse calculatorResponse = calculatorClient.calculate(request);

        System.out.println("Response from the server is " + calculatorResponse.getResponse());
    }

    private CalculatorServiceGrpc.CalculatorServiceBlockingStub getCalculatorServiceBlockingStub(ManagedChannel channel) {
        return CalculatorServiceGrpc.newBlockingStub(channel);
    }
}
