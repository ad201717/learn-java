package com.howe.learn.circuitbreaker.simplebreaker.metric;

import com.codahale.metrics.RatioGauge;

/**
 * @Author Karl
 * @Date 2017/7/21 15:27
 */
public class FusingMetric extends RatioGauge{

    public void cycle() {

    }


    @Override
    protected Ratio getRatio() {
        return Ratio;
    }
}
