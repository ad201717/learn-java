package com.howe.learn.spider.basic.core;

import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.processor.ProcessorTypeEnum;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @Author Karl
 * @Date 2017/1/4 16:52
 */
public final class SpiderJob {
    public static SpiderJob POISON = new SpiderJob();

    private String url;

    private ProcessorTypeEnum processorType;

    private ExtractorTypeEnum extractorType;

    public SpiderJob(){}

    public SpiderJob(String url, ProcessorTypeEnum processorType) {
        this.url = url;
        this.processorType = processorType;
    }

    public SpiderJob(String url, ExtractorTypeEnum extractorType) {
        this.url = url;
        this.processorType = ProcessorTypeEnum.EXTRACTOR;
        this.extractorType = extractorType;
    }

    public String getUrl() {
        return url;
    }

    public ProcessorTypeEnum getProcessorType() {
        return processorType;
    }

    public ExtractorTypeEnum getExtractorType() {
        return extractorType;
    }

    public void setExtractorType(ExtractorTypeEnum extractorType) {
        this.extractorType = extractorType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SpiderJob spiderJob = (SpiderJob) o;

        return new EqualsBuilder()
                .append(url, spiderJob.url)
                .append(processorType, spiderJob.processorType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(url)
                .append(processorType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "SpiderJob{" +
                "url='" + url + '\'' +
                ", processorType=" + processorType +
                ", extractorType=" + extractorType +
                '}';
    }
}
