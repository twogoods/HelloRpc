package com.tg.rpc.core.entity;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by twogoods on 2018/3/8.
 */
public class FutureHolder {
    private static ConcurrentHashMap<Long, FutureData<Response>> futureMap = new ConcurrentHashMap<>();

    public static void put(Long requestId, FutureData<Response> responseFutureData) {
        futureMap.put(requestId, responseFutureData);
    }

    public static FutureData<Response> get(Long requestId) {
        return futureMap.get(requestId);
    }

    public static void remove(Long requestId) {
        futureMap.remove(requestId);
    }
}
