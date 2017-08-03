package com.howe.learn.sequence;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID= 64 位二进制 (42(毫秒)+5(机器 ID)+5(业务编码)+12(重复累加)
 *
 * @Author Karl
 * @Date 2017/7/14 17:29
 */
public class Simple {


    private final static int MACHINE_BIT_LENGTH = 5;
    /**
     * 机器 ID
     */
    private final Integer machine;
    private final static int BIZ_BIT_LENGTH = 5;

    /**
     * 业务编码
     */
    private final Integer biz;

    /**
     * 累加
     */
    private final AtomicInteger counter = new AtomicInteger(0);
    private final static long SYSTEMTIME_MASK = ((1 << 42) - 1) << 22;
    private final static int MACHINE_MASK = ((1 << 5) - 1) << 17;
    private final static int BIZ_MASK = ((1 << 5) - 1) << 12;
    private final static int COUNTER_MASK = (1 << 12) - 1;

    public Simple(int machine, int biz) {
        this.machine = machine;
        this.biz = biz;
    }

    public Long next() {
        if (counter.incrementAndGet() > COUNTER_MASK) {
            counter.compareAndSet(COUNTER_MASK, 0);
        }

        return ((System.currentTimeMillis() << 22) & SYSTEMTIME_MASK )
                | (machine << 17)
                | (biz << 12)
                | counter.get();
    }

    public static void main(String[] args) {
        Simple sequence = new Simple(12,34);
        System.out.println(sequence.next());
        System.out.println(sequence.next());
        System.out.println(sequence.next());
        System.out.println(Long.toBinaryString(sequence.next()));
        System.out.println(Long.toBinaryString(sequence.next()));
        System.out.println(Long.toBinaryString(sequence.next()));
    }
}
