package com.tg.rpc.breaker;

import com.tg.rpc.breaker.Exception.BreakerException;
import com.tg.rpc.breaker.Exception.RequestExecutionException;
import com.tg.rpc.breaker.Exception.RequestRejectedException;
import com.tg.rpc.breaker.Exception.RequestTimeoutException;
import com.tg.rpc.breaker.concurrent.Executor;
import com.tg.rpc.breaker.concurrent.ExecutorFactory;
import com.tg.rpc.breaker.concurrent.task.Task;
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
import java.util.concurrent.*;

/**
 * Created by twogoods on 2017/7/21.
 * 规则：请求数大于某个阈值并且错误率超过50%触发熔断
 * 熔断后，每20秒过一个请求测试后端是否
 */
public class Breaker implements Executor {
    private static final Logger log = LoggerFactory.getLogger(Breaker.class);
    private BreakerProperty breakerProperty;
    private Map<Method, BreakerMetrics> metrics = new HashMap<Method, BreakerMetrics>();
    private ExecutorService executorService;
    private ThreadPoolStrategy strategy;

    public Breaker(BreakerProperty breakerProperty) {
        this.breakerProperty = breakerProperty;
        init();
    }

    private void init() {
        try {
            initMethodMetrics();
        } catch (ClassNotFoundException e) {
            throw new BreakerException(e);
        }
        initThreadPool();
        initCalcThread();
    }

    private void initMethodMetrics() throws ClassNotFoundException {
        List<String> classes = breakerProperty.getClazz();
        if (classes == null || classes.isEmpty()) {
            return;
        }
        for (String classStr : classes) {
            Class clazz = Class.forName(classStr);
            for (Method method : clazz.getMethods()) {
                metrics.put(method, new BreakerMetrics(method, breakerProperty));
            }
        }
    }

    private void initThreadPool() {
        executorService = ExecutorFactory.newExhaustedThreadPool(breakerProperty.getPoolSize());
        strategy = new SemaphoreStrategy(breakerProperty.getPoolSize());
    }

    private void initCalcThread() {
        Runnable calcTask = () -> {
            while (true) {
                try {
                    Thread.sleep(breakerProperty.getCalculateWindowInMillis());
                } catch (InterruptedException e) {
                }
                metrics.values().forEach(breakerMetrics -> {
                    breakerMetrics.check();
                });
            }
        };
        new Thread(calcTask).start();
    }

    @Override
    public Object execute(Task task) throws Throwable {
        BreakerMetrics breakerMetrics = metrics.get(task.getMetricsMethod());
        Validate.notNull(breakerMetrics, "can't get BreakerMetrics for Method: %s ,in class: %s ",
                task.getMetricsMethod().getName(), task.getMetricsMethod().getDeclaringClass().getName());
        if (breakerMetrics.isOpen()) {
            log.debug(String.format("breaker is open, reject execute task{%s.%s()}",
                    task.getMetricsMethod().getDeclaringClass().getName(), task.getMetricsMethod().getName()));
            breakerMetrics.increment(BreakerStatus.BREAKER_REJECT);
            if (task.supportFallback()) {
                return task.callFallback();
            } else {
                throw new RequestRejectedException(String.format("circuit-breaker is open! method : %s in %s can't fallback",
                        task.getMetricsMethod().getName(), task.getMetricsMethod().getDeclaringClass().getName()));
            }
        }
        if (strategy.isBusy()) {
            log.debug(String.format("no thread to execute task{ %s.%s() }",
                    task.getMetricsMethod().getDeclaringClass().getName(), task.getMetricsMethod().getName()));
            if (task.supportFallback()) {
                return task.callFallback();
            } else {
                throw new RequestRejectedException(String.format("method : %s in %s can't fallback",
                        task.getMetricsMethod().getName(), task.getMetricsMethod().getDeclaringClass().getName()));
            }
        }
        try {
            return call(task, breakerMetrics);
        } finally {
            strategy.release();
        }
    }

    private Object call(Task task, BreakerMetrics breakerMetrics) throws Throwable {
        Future<Object> callres = executorService.submit(task);
        if (breakerMetrics.inTestPhase()) {
            log.debug("inTestPhase, execute task: {}() in {}",
                    task.getMetricsMethod().getName(), task.getMetricsMethod().getDeclaringClass().getName());
        } else {
            log.debug("breaker closed, execute task: {}() in {}",
                    task.getMetricsMethod().getName(), task.getMetricsMethod().getDeclaringClass().getName());
        }
        try {
            Object result = callres.get(task.getTimeoutInMillis(), TimeUnit.MILLISECONDS);
            breakerMetrics.increment(BreakerStatus.SUCCESS);
            setTestPhase(true, breakerMetrics);
            return result;
        } catch (InterruptedException | TimeoutException e) {
            breakerMetrics.increment(BreakerStatus.TIMEOUT);
            setTestPhase(false, breakerMetrics);
            throw new RequestTimeoutException(e.getCause());
        } catch (ExecutionException e) {
            breakerMetrics.increment(BreakerStatus.ERROR);
            setTestPhase(false, breakerMetrics);
            throw new RequestExecutionException(e.getCause());
        } finally {
            setTestPhase(false, breakerMetrics);
        }
    }

    private void setTestPhase(boolean flag, BreakerMetrics breakerMetrics) {
        if (breakerMetrics.inTestPhase()) {
            log.debug("inTestPhase, execute task {}", flag ? "success!" : "fail!");
            breakerMetrics.singleTestPass(flag);
        }
    }
}