package com.github.rajasekharb.grpc.practice.service;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

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

    @Override
    public void primeFactorization(PrimeFactorizationRequest request, StreamObserver<PrimeFactorizationResponse> responseObserver) {
        int number = request.getMessage().getNumber();

        int divider = 2;

        while (number > 1) {//Repeat the steps as long as you have a number greater than 1
            if (number % divider == 0) {//Is the number divisible by the divider
                //Evenly divisible
                //Send the response to the client as and when you have a factor
                responseObserver.onNext(PrimeFactorizationResponse.newBuilder().setResponse(number).build());
                number = number / divider;
            } else {
                divider++;
            }
        }

        //Tell the client that you are done sending responses
        responseObserver.onCompleted();
    }
}
