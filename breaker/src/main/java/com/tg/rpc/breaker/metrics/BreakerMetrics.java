package com.tg.rpc.breaker.metrics;

import com.tg.rpc.breaker.Breaker;
import com.tg.rpc.breaker.BreakerProperty;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by twogoods on 2017/7/23.
 */
public class BreakerMetrics {
    private static final Logger log = LoggerFactory.getLogger(BreakerMetrics.class);
    @Getter
    private MetricsBucketArray metrics;
    @Getter
    private volatile boolean open = false;
    private volatile boolean testPhase = false;
    private Method metricsMethod;
    private BreakerProperty breakerProperty;
    private AtomicLong lastSingleTestTime = new AtomicLong();

    public void check() {
        MetricsBucketArray.MetricSnapshot snapshot = metrics.calculate();
        System.out.println(snapshot);
        log.debug("calculate {}.{} metrics: ", metricsMethod.getDeclaringClass().getName(), metricsMethod.getName(), snapshot.toString());
        if (open && (snapshot.getTotal()) < 1) {
            //统计的时间窗口期内没有请求进来，关闭熔断
            open = false;
            return;
        }
        if (open) {
            return;
        }
        if (snapshot.getTotal() > breakerProperty.getRequestCountThreshold()) {
            if (((float) snapshot.getError()) / ((float) snapshot.getTotal()) > breakerProperty.getErrorPercentageThreshold()) {
                open = true;
            }
        }
    }

    public boolean isOpen() {
        if (open && !letSingleTest()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean letSingleTest() {
        long last = lastSingleTestTime.get();
        long next = last + breakerProperty.getSingleTestWindowInMillis();
        long now = System.currentTimeMillis();
        //时间窗口里放行一个请求
        if (now > next && lastSingleTestTime.compareAndSet(last, now)) {
            testPhase = true;
            return testPhase;
        } else {
            return false;
        }
    }

    public void singleTestPass(boolean flag) {
        testPhase = false;
        if (flag) {
            open = false;
        }
    }

    public BreakerMetrics(Method metricsMethod, BreakerProperty breakerProperty) {
        this.breakerProperty = breakerProperty;
        this.metricsMethod = metricsMethod;
        metrics = new MetricsBucketArray(breakerProperty.getBucketNum());
    }

    public void increment(BreakerStatus breakerStatus) {
        metrics.peek().increment(breakerStatus);
    }

    public boolean inTestPhase() {
        return testPhase;
    }
}