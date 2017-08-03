package com.howe.learn.circuitbreaker.simplebreaker.metric;

import java.util.LinkedList;

import com.codahale.metrics.RatioGauge;

/**
 * @Author Karl
 * @Date 2017/7/21 15:37
 */
public class HistoryRatioGauge extends RatioGauge {

    private Class measuringObjCls;
    private LinkedList<RatioNumber> list;

    public HistoryRatioGauge(Class measuringObjCls, int size) {
        this.measuringObjCls = measuringObjCls;
        size = Math.max(1, size);
        list = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            list.add(new RatioNumber(0, 0));
        }
    }

    public static class RatioNumber {
        private volatile long numerator ;
        private volatile long denomarator;

        public RatioNumber(long numerator, long denomarator) {
            this.numerator = numerator;
            this.denomarator = denomarator;
        }

        public long getNumerator() {
            return numerator;
        }

        public void setNumerator(long numerator) {
            this.numerator = numerator;
        }

        public long getDenomarator() {
            return denomarator;
        }

        public void setDenomarator(long denomarator) {
            this.denomarator = denomarator;
        }
    }

    public synchronized void append(long numerator, long denomerator) {
        RatioNumber ratio = this.list.poll();
        ratio.setNumerator(numerator);
        ratio.setDenomarator(denomerator);
        this.list.add(ratio);
    }

    private synchronized Ratio sum() {
        int n = 0;
        int d = 0;
        for (RatioNumber ratio : this.list) {
            n += ratio.getNumerator();
            d += ratio.getDenomarator();
        }
        return Ratio.of(n, d);
    }

    @Override
    protected Ratio getRatio() {
        return sum();
    }
}
