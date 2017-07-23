package com.tg.rpc.breaker.metrics;

import com.tg.rpc.breaker.BreakerProperty;

/**
 * Created by twogoods on 2017/7/23.
 */
public class BreakerMetrics {
    private Metrics metrics;
    private volatile boolean isOpen = false;
    private volatile boolean inTestPhase = false;
    private BreakerProperty breakerProperty;
}