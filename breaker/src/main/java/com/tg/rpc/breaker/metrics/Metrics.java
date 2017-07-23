package com.tg.rpc.breaker.metrics;

import lombok.Getter;

/**
 * Created by twogoods on 2017/7/23.
 */
public class Metrics {
    private MetricsBucket[] buckets;
    private volatile int currentIndex;
    private int size;
    @Getter
    private MetricSnapshot lastCheck;


    @Getter
    class MetricSnapshot {
        private float total;
        private float error;
        private float circuitBreak;
    }
}
