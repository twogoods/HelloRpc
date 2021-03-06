package com.tg.rpc.breaker.metrics;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by twogoods on 2017/7/23.
 */
public class MetricsBucketArray {
    private MetricsBucket[] buckets;
    private volatile int currentIndex;
    private int size;
    @Getter
    private MetricSnapshot lastCheck;

    MetricsBucketArray(int size) {
        this.size = size;
        this.buckets = new MetricsBucket[size];
        for (int i = 0; i < size; i++) {
            buckets[i] = new MetricsBucket();
        }
        this.currentIndex = 0;
        this.lastCheck = new MetricSnapshot();
    }

    public MetricSnapshot calculate() {
        long total = 0;
        long success = 0;
        long error = 0;
        long circuitBreak = 0;

        for (MetricsBucket bucket : buckets) {
            total += bucket.errorCount.get() + bucket.timeoutCount.get() + bucket.successCount.get() + bucket.breakerRejcetCount.get();
            success += bucket.successCount.get();
            error += bucket.errorCount.get() + bucket.timeoutCount.get();
            circuitBreak += bucket.breakerRejcetCount.get();
        }
        currentIndex = ++currentIndex >= buckets.length ? 0 : currentIndex;
        buckets[currentIndex] = new MetricsBucket();
        MetricSnapshot check = new MetricSnapshot();
        check.error = error;
        check.success = success;
        check.total = total;
        check.circuitBreak = circuitBreak;
        lastCheck = check;
        return lastCheck;
    }

    public MetricsBucket peek() {
        return buckets[currentIndex];
    }


    @Getter
    @ToString
    class MetricSnapshot {
        private long total;
        private long success;
        private long error;
        private long circuitBreak;
    }
}
