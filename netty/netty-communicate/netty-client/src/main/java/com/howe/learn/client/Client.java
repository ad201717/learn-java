package com.howe.learn.client;

import com.howe.learn.common.RemoteResponse;
import com.howe.learn.common.request.CalcRequest;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author Karl
 * @Date 2016/10/28 11:04
 */
public class Client {

    public static void main(String[] args) throws InterruptedException {
        ConnectionManager.INSTANCE.init();
        RequestGenerator generator = new RequestGenerator();
        Connection connection = ConnectionManager.INSTANCE.getConnection();
        System.out.println("start test sync");
        for(int i = 0; i < 10; i++){
            Object response = connection.sendSync(generator.generate());
            System.out.println("response from sync-request :" + response);
        }
        System.out.println("test sync over..");
        System.out.println("start test async");
        for(int i = 0; i < 10; i++){
            connection.sendAsync(generator.generate(), new Callback() {

                public void call(RemoteResponse resp) {
                    System.out.println("response from async-request :" + resp);
                }
            });
        }
        try{
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("test async over..");
        ConnectionManager.INSTANCE.returnConnection(connection);
        ConnectionManager.INSTANCE.close();
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
