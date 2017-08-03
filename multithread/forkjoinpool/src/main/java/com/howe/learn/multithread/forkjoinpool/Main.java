package com.howe.learn.multithread.forkjoinpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

/**
 * @Author Karl
 * @Date 2017/6/30 11:58
 */
public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Future<Long> result = forkJoinPool.submit(new Calculator(0L, 123456789L));

        System.out.println(result.get());
        //7620789313366866

        Future<Long> result1 = forkJoinPool.submit(new Calculator1(0L, 123456789L));

        System.out.println(result1.get());
    }
}
