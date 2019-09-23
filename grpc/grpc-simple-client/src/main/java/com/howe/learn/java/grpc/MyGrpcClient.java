package com.howe.learn.java.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MyGrpcClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8082)
                .usePlaintext()
                .build();
        HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub = HelloServiceGrpc.newBlockingStub(channel);
        HelloResponse response = helloServiceBlockingStub.hello(HelloRequest.newBuilder()
                .setName("karl")
                .setAge(222)
                .addHobbies("tennis")
                .addHobbies("basketball")
                .putTags("he", "llo")
                .putTags("wo", "rld").build());
        System.out.println(response);
        channel.shutdown();
    }
}
