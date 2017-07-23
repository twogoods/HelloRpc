package com.tg.rpc.breaker;

import com.tg.rpc.breaker.metrics.BreakerMetrics;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by twogoods on 2017/7/21.
 * 规则：请求数大于某个阈值并且错误率超过50%触发熔断
 */
public class Breaker {
    private BreakerProperty breakerProperty;
    private Map<Method, BreakerMetrics> metrics = new HashMap<Method, BreakerMetrics>();

    private void init() throws ClassNotFoundException {


        List<String> classes = breakerProperty.getClazz();
        if (classes == null || classes.isEmpty()) {
            return;
        }
        for (String classStr : classes) {
            Class clazz = Class.forName(classStr);
            for (Method method : clazz.getMethods()) {
                metrics.put(method, new BreakerMetrics());
            }
        }
    }

    public Object execute(Method method, Object[] args) {

        return null;
    }
}
