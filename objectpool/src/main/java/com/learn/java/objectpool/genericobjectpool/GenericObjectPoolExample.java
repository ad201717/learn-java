package com.learn.java.objectpool.genericobjectpool;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.ObjectPoolFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.util.concurrent.atomic.AtomicInteger;

public class GenericObjectPoolExample {

    public static void main(String[] args) {
        PoolableObjectFactory<Object> poolFactory = new PoolableObjectFactory<Object>() {
            private AtomicInteger counter = new AtomicInteger(0);
            @Override
            public Object makeObject() throws Exception {
                return counter.incrementAndGet();
            }

            @Override
            public void destroyObject(Object o) throws Exception {

            }

            @Override
            public boolean validateObject(Object o) {
                return false;
            }

            @Override
            public void activateObject(Object o) throws Exception {

            }

            @Override
            public void passivateObject(Object o) throws Exception {

            }
        };

        GenericObjectPool<Object> pool = new GenericObjectPool<>(poolFactory);
        try {
            Object o1 = pool.borrowObject();
            System.out.println("o1:" + o1);
            Object o2 = pool.borrowObject();
            System.out.println("o2:" + o2);
            pool.returnObject(o1);
            Object o3 = pool.borrowObject();
            System.out.println("o3:" + o3);
            Object o4 = pool.borrowObject();
            System.out.println("o4:" + o4);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
