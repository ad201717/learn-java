package com.howe.learn.multithread.forkjoinpool;

import java.util.concurrent.RecursiveTask;

/**
 * @Author Karl
 * @Date 2017/6/30 11:55
 */
public class Calculator1 extends RecursiveTask<Long> {

    private static final Long THRESHOLD = 100000L;
    private long start ;
    private long end ;

    public Calculator1(long start, long end) {
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
            Calculator1 left = new Calculator1(start, middle);
            Calculator1 right = new Calculator1(middle + 1, end);

            invokeAll(left, right);
            sum = left.getRawResult() + right.getRawResult();
        }
        return sum;
    }
}
