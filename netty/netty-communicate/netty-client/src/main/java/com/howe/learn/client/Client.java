package com.howe.learn.client;

import com.howe.learn.common.request.CalcRequest;

import java.util.Random;

/**
 * @Author Karl
 * @Date 2016/10/28 11:04
 */
public class Client {

    public static void main(String[] args) throws InterruptedException {
        ClientManager.INSTANCE.init();
        RequestGenerator generator = new RequestGenerator();
        ClientManager.Connection connection = ClientManager.INSTANCE.getConnection();
        for(int i = 0; i < 100; i++){
            Object response = connection.sendSync(generator.generate());
            System.out.println(response);
        }
        System.out.println("over..");
        ClientManager.INSTANCE.returnConnection(connection);
        ClientManager.INSTANCE.close();
    }

    static class RequestGenerator {
        Random random = new Random();

        CalcRequest generate(){
            int x = random.nextInt(100);
            CalcRequest.CalMethod calMethod = CalcRequest.CalMethod.values()[random.nextInt(4)];
            int y = 1 + random.nextInt(20);
            CalcRequest request = new CalcRequest();
            request.setX(x);
            request.setCalMethod(calMethod);
            request.setY(y);
            return request;
        }
    }
}
