package com.howe.learn.circuitbreaker.simplebreaker.metric;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import org.junit.Test;

/**
 * @Author Karl
 * @Date 2017/7/21 11:24
 */
public class MetricTest {

    @Test
    public void testMetric() throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter
                .forRegistry(registry)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .convertRatesTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(500, TimeUnit.MILLISECONDS);
        Counter counter = registry.counter("counter");
        counter.inc();
        TimeUnit.MILLISECONDS.sleep(1000);
        counter.inc(12);
        TimeUnit.MILLISECONDS.sleep(2000);
        reporter.stop();
    }
}
