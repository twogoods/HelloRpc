package com.tg.rpc.breaker.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by twogoods on 2017/7/23.
 */
public class ExecutorFactory {
    public static ExecutorService newExhaustedThreadPool(int corePoolSize, int maximumPoolSize) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                3000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(corePoolSize + maximumPoolSize),
                new DefaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy()
        );
    }

    public static ExecutorService newExhaustedThreadPool(int poolSize) {
        return new ThreadPoolExecutor(poolSize, (poolSize >> 1) + poolSize,
                3000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(((poolSize >> 1) + (poolSize << 1))),
                new DefaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy()
        );
    }

    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "Breaker-thread-pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
