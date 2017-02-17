package com.tg.rpc.core.entity;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by twogoods on 17/2/17.
 */
public class QueueHolder {
    private static ConcurrentHashMap<Long, BlockingQueue<Response>> queueMap = new ConcurrentHashMap<>();

    public static void put(Long requestId, BlockingQueue<Response> queue) {
        queueMap.put(requestId, queue);
    }

    public static BlockingQueue<Response> get(Long requestId) {
        return queueMap.get(requestId);
    }
    public static void remove(Long requestId) {
        queueMap.remove(requestId);
    }
}
