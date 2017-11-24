package com.altoros.meetup.threadlocal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author Nikita Gorbachevski
 */
public class TLocal {

    static class Task implements Runnable {
        private final ThreadLocal<List<Integer>> counts = ThreadLocal.withInitial(ArrayList::new);
        private volatile List<Integer> result = new ArrayList<>();
        private BlockingQueue<Object> queue;

        public Task(BlockingQueue<Object> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            int count = 0;
            long start = System.nanoTime();
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    queue.take();
                    count++;
                    long end = System.nanoTime();
                    if ((end - start) >= TimeUnit.SECONDS.toNanos(1)) {
                        counts.get().add(count);
                        count = 0;
                        start = end;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // the last value
            counts.get().add(count);
            // copy the result cause it's not possible
            // to access thread local variable outside of this thread
            result = counts.get();
        }

        public List<Integer> getCounts() {
            return result;
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        BlockingQueue<Object> blockingQueue = new LinkedBlockingQueue<>();
        Task t1 = new Task(blockingQueue);
        Task t2 = new Task(blockingQueue);
        Task t3 = new Task(blockingQueue);
        executorService.submit(t1);
        executorService.submit(t2);
        executorService.submit(t3);

        for (int i = 0; i < 50; i++) {
            blockingQueue.add(new Object());
            Thread.sleep(100);
        }

        executorService.shutdownNow();
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("t1 " + t1.getCounts());
        System.out.println("t2 " + t2.getCounts());
        System.out.println("t3 " + t3.getCounts());

        int total = Stream.concat(Stream.concat(t1.getCounts().stream(), t2.getCounts().stream()), t3.getCounts().stream())
                .reduce(0, (a, b) -> a + b);
        // 50 as expected
        System.out.println(total);
    }
}
