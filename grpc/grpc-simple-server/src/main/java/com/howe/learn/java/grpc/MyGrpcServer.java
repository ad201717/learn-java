package com.howe.learn.java.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class MyGrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8082)
                .addService(new HelloService())
                .build();
        System.out.println("starting server....");
        server.start();
        System.out.println("server started.");
        server.awaitTermination();
    }
}
