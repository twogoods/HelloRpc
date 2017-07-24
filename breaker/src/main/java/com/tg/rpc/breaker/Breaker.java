package com.tg.rpc.breaker;

import com.tg.rpc.breaker.Exception.RequestRejectedException;
import com.tg.rpc.breaker.concurrent.Executor;
import com.tg.rpc.breaker.concurrent.ExecutorFactory;
import com.tg.rpc.breaker.metrics.BreakerMetrics;
import com.tg.rpc.breaker.metrics.BreakerStatus;
import com.tg.rpc.breaker.strategy.SemaphoreStrategy;
import com.tg.rpc.breaker.strategy.ThreadPoolStrategy;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by twogoods on 2017/7/21.
 * 规则：请求数大于某个阈值并且错误率超过50%触发熔断
 * 熔断后，每10秒过一个请求
 */
public class Breaker implements Executor {
    private static final Logger log = LoggerFactory.getLogger(Breaker.class);
    private BreakerProperty breakerProperty;
    private Map<Method, BreakerMetrics> metrics = new HashMap<Method, BreakerMetrics>();
    private ExecutorService executorService;
    private ThreadPoolStrategy strategy;

    private void init() throws ClassNotFoundException {
        List<String> classes = breakerProperty.getClazz();
        if (classes == null || classes.isEmpty()) {
            return;
        }
        for (String classStr : classes) {
            Class clazz = Class.forName(classStr);
            for (Method method : clazz.getMethods()) {
                metrics.put(method, new BreakerMetrics(breakerProperty));
            }
        }
    }

    private void initThreadPool() {
        executorService = ExecutorFactory.newExhaustedThreadPool(breakerProperty.getPoolSize());
        strategy = new SemaphoreStrategy(breakerProperty.getPoolSize());
    }

    private void initCalaThread() {
        Runnable calcTask = () -> {
            metrics.values().forEach(breakerMetrics -> {
                breakerMetrics.check();
            });
        };
        new Thread(calcTask).run();
    }

    @Override
    public Object execute(Task task) throws Throwable {
        BreakerMetrics breakerMetrics = metrics.get(task.getMethod());
        Validate.notNull(breakerMetrics, "can't get BreakerMetrics for Method: %s ,in class: %s ",
                task.getMethod().getName(), task.getMethod().getDeclaringClass().getName());
        if (breakerMetrics.isOpen()) {
            breakerMetrics.increment(BreakerStatus.BREAKER_REJECT);
            if (task.supportFallback()) {
                return task.callFallback();
            } else {
                throw new RequestRejectedException(String.format("circuit-breaker is open! method : %s in %s can't fallback",
                        task.getMethod().getName(), task.getMethod().getDeclaringClass().getName()));
            }
        }
        if (strategy.isBusy()) {
            if (task.supportFallback()) {
                return task.callFallback();
            } else {
                throw new RequestRejectedException(String.format("method : %s in %s can't fallback",
                        task.getMethod().getName(), task.getMethod().getDeclaringClass().getName()));
            }
        }
        Future callres = executorService.submit(task);
        try {
            Object result = callres.get(task.getTimeoutInMillis(), TimeUnit.MILLISECONDS);
            if (breakerMetrics.inTestPhase()) {
                //如果这次调用是测试服务是否可用的调用，结果写回
                breakerMetrics.singleTestPass(true);
            }
        } catch (TimeoutException e) {
            breakerMetrics.increment(BreakerStatus.TIMEOUT);
        }
        return null;
    }
}