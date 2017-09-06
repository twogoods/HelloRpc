package com.tg.rpc.breaker.metrics;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by twogoods on 2017/7/23.
 */
@Getter
public class MetricsBucket {
    public final AtomicLong errorCount = new AtomicLong(0);
    public final AtomicLong timeoutCount = new AtomicLong(0);
    public final AtomicLong successCount = new AtomicLong(0);
    public final AtomicLong breakerRejcetCount = new AtomicLong(0);

    void increment(BreakerStatus type) {
        switch (type) {
            case SUCCESS:
                successCount.getAndIncrement();
                break;
            case ERROR:
                errorCount.getAndIncrement();
                break;
            case TIMEOUT:
                timeoutCount.getAndIncrement();
                break;
            case BREAKER_REJECT:
                breakerRejcetCount.getAndIncrement();
                break;
            default:
                break;
        }
    }
}
