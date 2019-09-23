package com.howe.learn.java.grpc;

import io.grpc.stub.StreamObserver;

public class HelloService extends HelloServiceGrpc.HelloServiceImplBase{
    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        System.out.println(request);
        String greeting = "Hi " + request.getName() + " you are " + request.getAge() + " years old your hobby is " + request.getHobbiesList()
                + " your tags " + request.getTagsMap();
        HelloResponse response = HelloResponse.newBuilder().setGreeting(greeting).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
