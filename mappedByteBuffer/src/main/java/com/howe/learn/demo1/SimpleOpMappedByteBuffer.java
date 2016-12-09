package com.howe.learn.demo1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * java内存映射文件
 *
 * 优势：
 *  1.提供了java有可能达到的最快IO
 *  2.关键优势是操作系统负责真正的写入，即使程序在刚刚写入内存之后就挂掉，操作系统仍会将内存中的数据写入文件
 *  3.共享内存，可以被多个进程同时访问，起到低时延的共享内存的作用
 *
 * @Author Karl
 * @Date 2016/12/9 10:57
 */
public class SimpleOpMappedByteBuffer {

    private static final int totalSize = 10 * 1024 * 1024;

    public static void main(String args[]) throws IOException {
        RandomAccessFile file = new RandomAccessFile("/mapped.dat", "rw");
        MappedByteBuffer mappedByteBuffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, totalSize);
        System.out.println("position:" + mappedByteBuffer.position());
        System.out.println("capacity:" + mappedByteBuffer.capacity());
        System.out.println("hasRemaining:" + mappedByteBuffer.hasRemaining());
        System.out.println("limit:" + mappedByteBuffer.limit());
        for(int i = 0; i < totalSize/16 ; i++){
            mappedByteBuffer.put((byte)'A');
            mappedByteBuffer.put((byte)'B');
            mappedByteBuffer.put((byte)'C');
            mappedByteBuffer.put((byte)'D');
            mappedByteBuffer.putInt(i);
            mappedByteBuffer.putLong(i);
        }
        System.out.println("position:" + mappedByteBuffer.position());
        System.out.println("capacity:" + mappedByteBuffer.capacity());
        System.out.println("hasRemaining:" + mappedByteBuffer.hasRemaining());
        System.out.println("limit:" + mappedByteBuffer.limit());
        for(int i = 4; i < 100; ){
            mappedByteBuffer.position(i);
            System.out.println(i + " int:" + mappedByteBuffer.getInt());
            System.out.println(i + " long:" + mappedByteBuffer.getLong());
            i += 16;
        }
    }
}
