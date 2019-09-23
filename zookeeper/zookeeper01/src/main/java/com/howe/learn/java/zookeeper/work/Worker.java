package com.howe.learn.java.zookeeper.work;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import static com.howe.learn.java.zookeeper.work.Constants.PATH_ZOOKEEPER01_QUOTA;
import static com.howe.learn.java.zookeeper.work.Constants.PATH_ZOOKEEPER01_WORKER;

@Setter
@Getter
@ToString
@Slf4j
public class Worker implements Runnable {

    private String type;

    private String id;

    private CuratorFramework curator;

    public Worker(CuratorFramework curator) {
        this.curator = curator;
    }

    @Override
    public void run() {
        init();
        work();
    }

    private void init() {
        try {
            String path = curator.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(PATH_ZOOKEEPER01_WORKER + "/");
            this.id = path.substring(path.lastIndexOf("/") + 1);
            NodeCache nodeCache = new NodeCache(curator, path);
            nodeCache.start(true);
            nodeCache.getListenable().addListener(()->{
                this.type = new String(nodeCache.getCurrentData().getData());
            });
            ChildData currentData = nodeCache.getCurrentData();
            if (null != currentData && null != currentData.getData() && currentData.getData().length > 0) {
                this.type = new String(currentData.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void work() {
        while (true) {
            if (null != type) {
                log.info("worker({})[{}] is working.", id, type);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.info("worker({})[{}] stopped.", id, type);
                return;
            }
        }
    }
}
