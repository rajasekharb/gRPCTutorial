package com.github.rajasekharb.grpc.practice.service;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    //Unary
    @Override
    public void calculate(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
        //Extract the required fields
        Message message = request.getMessage();
        int firstNumber = message.getFirstNumber();
        int secondNumber = message.getSecondNumber();

        //Do the processing
        int sum = firstNumber + secondNumber;

        //Create the response
        CalculatorResponse response = CalculatorResponse.newBuilder().setResponse(sum).build();

        //Send the response
        responseObserver.onNext(response);

        //Send the completion message
        responseObserver.onCompleted();
    }

    //Server streaming
    @Override
    public void primeFactorization(PrimeFactorizationRequest request, StreamObserver<PrimeFactorizationResponse> responseObserver) {
        int number = request.getMessage().getNumber();

        int divisor = 2;

        while (number > 1) {//Repeat the steps as long as you have a number greater than 1
            if (number % divisor == 0) {//Is the number divisible by the divider
                //Evenly divisible
                //Send the response to the client as and when you have a factor
                responseObserver.onNext(PrimeFactorizationResponse.newBuilder().setResponse(divisor).build());
                number = number / divisor;
            } else {
                divisor++;
            }
        }

        //Tell the client that you are done sending responses
        responseObserver.onCompleted();
    }

    //Client streaming
    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {
        final Double[] array = new Double[2];
        array[0] = 0d;
        array[1] = 0d;

        return new StreamObserver<>() {
            @Override
            public void onNext(ComputeAverageRequest value) {
                //Client streaming messages are received here
                array[0]++;
                array[1] = array[1] + value.getNumber();
            }

            @Override
            public void onError(Throwable t) {
                //If client sends an error it is received here
            }

            @Override
            public void onCompleted() {
                //If the client sends completed message, it is received here

                //Since the client has completed sending the request(s), send a response to the client
                responseObserver.onNext(ComputeAverageResponse.newBuilder().setResult(array[1] / array[0]).build());
                //Also send the response that the server is done processing the response
                responseObserver.onCompleted();
            }
        };
    }


}
