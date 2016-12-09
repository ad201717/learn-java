package com.howe.learn.redis;

import junit.framework.Assert;
import org.junit.Test;

/**
 * 请求限速
 *
 * @Author Karl
 * @Date 2016/12/9 17:21
 */
public class RequestLimiterTest {

    @Test
    public void test(){
        String method = "test";
        String ip = "127.0.0.1";
        int maxTps = 200;
        for(int i = 0; i < maxTps ; i++){
            //can pass
            Assert.assertTrue(RequestLimiter.getInstance().canPass(method, ip, maxTps));
        }
        //access limit, can`t pass
        Assert.assertTrue( ! RequestLimiter.getInstance().canPass(method, ip, maxTps));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //reset , sure it can pass
        Assert.assertTrue(RequestLimiter.getInstance().canPass(method, ip, maxTps));

        RequestLimiter.getInstance().close();
    }

}
