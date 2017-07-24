package com.tg.rpc.breaker;

import lombok.Data;

import java.util.List;

/**
 * Created by twogoods on 2017/7/23.
 */
@Data
public class BreakerProperty {
    private int poolSize = 10;
    private List<String> clazz;
    private long calculateWindowInMillis = 3000l;
    private int bucketNum = 10;
    private float errorPercentageThreshold = 0.5f;
    private int requestCountThreshold = 60;
    private long singleTestWindowInMillis = 20000l;
}
