package com.howe.learn.circuitbreaker.simplebreaker.metric;

import java.util.concurrent.ConcurrentHashMap;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;

/**
 * @Author Karl
 * @Date 2017/7/21 14:02
 */
public enum MeterCenter {
    INSTANCE;

    private static final MetricRegistry registry = new MetricRegistry();
    private static final ConcurrentHashMap<String, Metric>
}
