package com.howe.learn.java.zookeeper.controller;

import com.howe.learn.java.zookeeper.work.Quota;
import com.howe.learn.java.zookeeper.work.WorkerPool;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
public class ZookeeperController {

    @Autowired
    private WorkerPool workerPool;

    @RequestMapping("/quota")
    public ResponseEntity quota(@RequestBody Map<String, Integer> quota) {
        try {
            workerPool.setQuota(quota);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/scale")
    public ResponseEntity scale(@RequestParam("size") int size) {
        workerPool.scale(size);
        return new ResponseEntity(HttpStatus.OK);
    }

}
