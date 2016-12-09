package com.howe.learn.benchmark;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author Karl
 * @Date 2016/12/9 14:10
 */
public class Benchmark {

    public static void main(String[] args){
        final int total = 1024 * 1024 * 1024;
        test(new Tester() {
            public String name() {
                return "generalAppend";
            }

            public void test() {
                generalAppend(total);
            }
        });

        test(new Tester() {
            public String name() {
                return "mappedByteBufAppend";
            }

            public void test() {
                mappedByteBufAppend(total);
            }
        });

        test(new Tester() {
            public String name() {
                return "generalRead";
            }

            public void test() {
                generalRead(total);
            }
        });

        test(new Tester() {
            public String name() {
                return "mappedByteBufRead";
            }

            public void test() {
                mappedByteBufRead(total);
            }
        });
    }

    private static void generalAppend(int total){
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(new File("/generalAppend.dat")));
            for(int i = 0; i < total; i++){
                bos.write((byte)i);
            }
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != bos){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void generalRead(int total){
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(new File("/generalAppend.dat")));
            for(int i = 0; i < total; i++){
                bis.read();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != bis){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void mappedByteBufAppend(int total){
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile("/mappedByteBufAppend.dat", "rw");
            MappedByteBuffer mappedByteBuffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, total);
            for(int i = 0; i < total; i++){
                mappedByteBuffer.put((byte)i);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != file){
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void mappedByteBufRead(int total){
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile("/mappedByteBufAppend.dat", "r");
            MappedByteBuffer mappedByteBuffer = file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, total);
            for(int i = 0; i < total; i++){
                mappedByteBuffer.get();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != file){
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void test(Tester tester){
        long start = System.currentTimeMillis();
        tester.test();
        System.out.println("testor:"+tester.name()+" cost(ms):" + (System.currentTimeMillis() - start));
    }

    static interface Tester {
        String name();
        void test();
    }
}
