package com.howe.learn.java.zookeeper.work;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.howe.learn.java.zookeeper.work.Constants.PATH_ZOOKEEPER01_QUOTA;

@Service
@Slf4j
public class WorkerPool implements SmartLifecycle {

    @Autowired
    private CuratorFramework curator;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 32, 60000, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>(), new ThreadPoolExecutor.DiscardPolicy());

    private Map<String, Integer> quota = new HashMap<>();

    private Manager manager;

    private Deque<Worker> workers = new LinkedBlockingDeque<>();

    private volatile boolean running = false;

    private void init() {
        try {
            List<String> strings = curator.getChildren()
                    .forPath(PATH_ZOOKEEPER01_QUOTA);
            if (!CollectionUtils.isEmpty(strings)) {
                for (String key : strings) {
                    byte[] bytes = curator.getData().forPath(PATH_ZOOKEEPER01_QUOTA + "/" + key);
                    quota.put(key, Integer.parseInt(new String(bytes)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setQuota(Map<String, Integer> newQuota) throws Exception {
        Set<String> toAdd = newQuota.keySet();
        Set<String> toDel = Collections.EMPTY_SET;
        Set<String> toUpdate = Collections.EMPTY_SET;
        if (null != this.quota) {
            toAdd = Sets.difference(newQuota.keySet(), this.quota.keySet());
            toDel = Sets.difference(this.quota.keySet(), newQuota.keySet());
            toUpdate = Sets.filter(newQuota.keySet(), s -> this.quota.containsKey(s) && !newQuota.get(s).equals(this.quota.get(s)));
        }
        log.info("[pool] quota to add [{}]", toAdd);
        log.info("[pool] quota to del [{}]", toDel);
        log.info("[pool] quota to update [{}]", toUpdate);

        toAdd.stream()
                .forEach(type->{
                    try {
                        curator.create()
                                .creatingParentsIfNeeded()
                                .withMode(CreateMode.PERSISTENT)
                                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                                .forPath(PATH_ZOOKEEPER01_QUOTA + "/" + type, String.valueOf(newQuota.get(type)).getBytes());
                    } catch (Exception e) {
                        log.error("ss", e);
                    }
                });
        toDel.stream()
                .forEach(type->{
                    try {
                        curator.delete()
                                .forPath(PATH_ZOOKEEPER01_QUOTA + "/" + type);
                    } catch (Exception e) {
                        log.error("ss", e);
                    }
                });
        toUpdate.stream()
                .forEach(type->{
                    try {
                        curator.setData()
                                .forPath(PATH_ZOOKEEPER01_QUOTA + "/" + type, String.valueOf(newQuota.get(type)).getBytes());
                    } catch (Exception e) {
                        log.error("ss", e);
                    }
                });
        this.quota = newQuota;
    }

    public void scale(int size) {
        for (int i = 0; i < size; i++) {
            Worker worker = new Worker(curator);
            workers.offer(worker);
            executor.execute(worker);
        }
    }

    public void shrink() {

    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        stop();
    }

    @Override
    public void start() {
        init();
        manager = new Manager(curator);
        manager.start();
        this.running = true;
    }

    @Override
    public void stop() {
        executor.shutdown();
        try {
            executor.awaitTermination(60000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return 0;
    }

}
