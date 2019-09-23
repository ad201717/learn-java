package com.howe.learn.java.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.howe.learn.java.protobuf.proto.PersonModel;

public class PersonUseCase {

    public static void main(String[] args) {
        PersonModel.Person person = PersonModel.Person.newBuilder()
                .setId(2)
                .setName("test")
                .setEmail("test@qq.com")
                .build();
        byte[] bytes = person.toByteArray();
        for (byte b : bytes) {
            System.out.print(b);
        }
        System.out.println();
        System.out.print(Integer.toBinaryString(2));
        System.out.print(" ");
        for (byte b : "test".getBytes()) {
            System.out.print(b);
        }
        System.out.print(" ");
        for (byte b : "test@qq.com".getBytes()) {
            System.out.print(b);
        }
    }
}
