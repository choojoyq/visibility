package com.altoros.meetup.atomicity;

import java.util.concurrent.CountDownLatch;

/**
 * @author Nikita Gorbachevski
 */
public class Locking {

    // read-modify-write
    public static class Counter {

        private long counter;

        public void increment() {
            // temp = old;
            // old = new;
            // new = temp;
            counter++;
        }

        public long getCount() {
            return counter;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final Counter counter = new Counter();
        int numberOfThreads = 20;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                    for (int i = 0; i < 100; i++) {
                        counter.increment();
                    }
                    endLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        endLatch.await();
        System.out.println(counter.getCount());
    }
}
