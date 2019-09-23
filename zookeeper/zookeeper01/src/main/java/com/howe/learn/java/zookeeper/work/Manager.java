package com.howe.learn.java.zookeeper.work;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.howe.learn.java.zookeeper.work.Constants.PATH_ZOOKEEPER01_QUOTA;
import static com.howe.learn.java.zookeeper.work.Constants.PATH_ZOOKEEPER01_WORKER;

@Slf4j
public class Manager {

    private CuratorFramework curator;

    public Manager(CuratorFramework curator) {
        this.curator = curator;
    }

    private ConcurrentHashMap<String, Integer> quotaMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, List<String>> typeToWorkersMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> workerToTypeMap = new ConcurrentHashMap<>();

    public void start() {
        try {
            if (null == curator.checkExists()
                    .forPath(PATH_ZOOKEEPER01_WORKER)) {
                curator.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(PATH_ZOOKEEPER01_WORKER);
            }

            PathChildrenCache cache = new PathChildrenCache(curator, PATH_ZOOKEEPER01_WORKER, true);
            cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            cache.getListenable().addListener((cf, event) -> {
                log.info("[manage] event:{}", event);
            });
            cache.getCurrentData().stream()
                    .forEach(childData -> workerToTypeMap.put(childData.getPath().substring(childData.getPath().lastIndexOf("/") + 1), new String(childData.getData())));
            log.info("[manage] workerToTypeMap []", workerToTypeMap);

            PathChildrenCache taskTypesCache = new PathChildrenCache(curator, PATH_ZOOKEEPER01_QUOTA, true);
            taskTypesCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            taskTypesCache.getListenable().addListener(((client, event) -> {
                log.info("[manage] event:{}", event);
            }));
            taskTypesCache.getCurrentData().stream().forEach(childData -> {
                String taskType = childData.getPath().substring(childData.getPath().lastIndexOf("/") + 1);
                quotaMap.put(taskType, Integer.parseInt(new String(childData.getData())));
                try {
                    PathChildrenCache taskTypeCache = new PathChildrenCache(curator, childData.getPath(), true);
                    taskTypeCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
                    taskTypeCache.getListenable().addListener((client, event) -> {
                        log.info("[manage] event:{}", event);
                    });
                    List<String> workerIds = taskTypeCache.getCurrentData().stream().map(ChildData::getData).map(bytes -> new String(bytes)).collect(Collectors.toList());
                    typeToWorkersMap.put(taskType, workerIds);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            log.info("[manage] quotaMap []", quotaMap);
            log.info("[manage] typeToWorkersMap []", typeToWorkersMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
