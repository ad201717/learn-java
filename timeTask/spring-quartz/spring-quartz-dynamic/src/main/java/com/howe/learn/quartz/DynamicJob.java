package com.howe.learn.quartz;

import org.quartz.Job;

import java.io.Serializable;
import java.util.Map;

public interface DynamicJob extends Job, Serializable {
    String getKey();

    long getStartDelayInMills();

    Map<String, Object> getJobData();

}
