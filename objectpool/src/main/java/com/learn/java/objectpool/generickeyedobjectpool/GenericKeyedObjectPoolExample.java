package com.learn.java.objectpool.generickeyedobjectpool;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;

import java.util.concurrent.atomic.AtomicInteger;

public class GenericKeyedObjectPoolExample {

    public static void main(String[] args) {
        KeyedPoolableObjectFactory<String, Object> poolFactory = new KeyedPoolableObjectFactory<String, Object>(){
            private AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Object makeObject(String s) throws Exception {
                String str = s + ":" + counter.incrementAndGet();
                return str;
            }

            @Override
            public void destroyObject(String s, Object o) throws Exception {

            }

            @Override
            public boolean validateObject(String s, Object o) {
                return false;
            }

            @Override
            public void activateObject(String s, Object o) throws Exception {

            }

            @Override
            public void passivateObject(String s, Object o) throws Exception {

            }
        };
        GenericKeyedObjectPool<String, Object> pool = new GenericKeyedObjectPool<String, Object>(poolFactory);
        try {
            Object o1 = pool.borrowObject("hello");
            System.out.println("o1:" + o1);
            Object o2 = pool.borrowObject("hello");
            System.out.println("o2:" + o2);
            pool.returnObject("hello", o2);
            Object o3 = pool.borrowObject("hello");
            System.out.println("o3:" + o3);
            Object o4 = pool.borrowObject("hello");
            System.out.println("o4:" + o4);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                pool.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
