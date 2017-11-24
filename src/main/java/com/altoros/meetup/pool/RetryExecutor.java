package com.altoros.meetup.pool;

/**
 * @author Nikita Gorbachevski
 */

import java.util.concurrent.*;

public class RetryExecutor extends ThreadPoolExecutor {

    public RetryExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public static void main(String[] args) throws Exception {
        RetryExecutor executor = RetryExecutor.newFixedRetryExecutor(2);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("OPACHA " + Thread.currentThread().getId());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                    Thread.currentThread().interrupt();
                }
            }
        });
        Future<Void> result = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                System.out.println("OPACHA " + Thread.currentThread().getId());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                    Thread.currentThread().interrupt();
                }
                return null;
            }
        });
        System.out.println("Before get");
        result.get();
        System.out.println("After get");
        Thread.sleep(5000);
        executor.shutdownNow();
        boolean b = executor.awaitTermination(1, TimeUnit.SECONDS);
        System.out.println(b);
    }

    public static RetryExecutor newFixedRetryExecutor(int nThreads) {
        return new RetryExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        System.out.println("after execute");
        Runnable runnable = ((MyFutureTask) r).getRunnable();
        Callable<?> callable = ((MyFutureTask) r).getCallable();
        if (!Thread.currentThread().isInterrupted()) {
            if (runnable != null) {
                submit(runnable);
            } else {
                submit(callable);
            }
        }
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new MyFutureTask<>(runnable, value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new MyFutureTask<>(callable);
    }

    private static class MyFutureTask<V> extends FutureTask<V> {
        public MyFutureTask(Callable<V> callable) {
            super(callable);
        }

        public MyFutureTask(Runnable runnable, V result) {
            super(runnable, result);
        }

        private volatile Runnable runnable;
        private volatile Callable<V> callable;

        @Override
        protected void done() {
            super.done();
            Object task = JobDiscover.findRealTask(this);
            if (task instanceof Runnable) {
                runnable = (Runnable) task;
            } else {
                callable = (Callable<V>) task;
            }
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public Callable<V> getCallable() {
            return callable;
        }
    }
}

