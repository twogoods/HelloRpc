package com.tg.rpc.breaker;

import lombok.Data;

import java.util.List;

/**
 * Created by twogoods on 2017/7/23.
 */
@Data
public class BreakerProperty {
    private List<String> clazz;
    private long calculateWindowInMillis = 3000l;
    private long bucketNum = 10;
    private float errorPercentageThreshold = 0.5f;
    private int requestCountThreshold = 60;
}
