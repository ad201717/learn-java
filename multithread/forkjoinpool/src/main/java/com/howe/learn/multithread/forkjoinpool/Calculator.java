package com.howe.learn.multithread.forkjoinpool;

import java.util.concurrent.RecursiveTask;

/**
 * @Author Karl
 * @Date 2017/6/30 11:55
 */
public class Calculator extends RecursiveTask<Long> {

    private static final Long THRESHOLD = 100000L;
    private long start ;
    private long end ;

    public Calculator(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        long sum = 0;
        if((start - end) < THRESHOLD){
            for(long i = start; i< end;i++){
                sum += i;
            }
        }else{
            long middle = (start + end) /2;
            Calculator left = new Calculator(start, middle);
            Calculator right = new Calculator(middle + 1, end);
            left.fork();
            right.fork();

            sum = left.join() + right.join();
        }
        return sum;
    }
}
