package com.altoros.meetup.threadlocal;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Nikita Gorbachevski
 */
public class TimeFilter {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Work());
        }
        executorService.shutdown();
    }

    public static class Work implements Runnable {

        private ThreadLocal<Long> start = new ThreadLocal<>();

        @Override
        public void run() {
            start.set(System.currentTimeMillis());
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {

            } finally {
                System.out.println("Thread " + Thread.currentThread().getId()
                        + " Worked for " + (System.currentTimeMillis() - start.get()));
            }
        }
    }
}
