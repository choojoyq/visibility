package com.altoros.meetup.interruption;

import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Nikita Gorbachevski
 */
public class Factorial {

    public int calculate(long timeout) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Calculation calculation = new Calculation(countDownLatch);
        Thread t = new Thread(calculation);
        t.start();
        Thread.sleep(timeout);
        t.interrupt();
        countDownLatch.await();
        return calculation.getFactorialResponse();
    }

    public int calculateFuture(long timeout) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Calculation calculation = new Calculation(countDownLatch);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(calculation);
        Thread.sleep(timeout);
        future.cancel(true);
        countDownLatch.await();
        executorService.shutdown();
        return calculation.getFactorialResponse();
    }

    public static void main(String[] args) throws Exception {
        Factorial factorial = new Factorial();
        System.out.println(factorial.calculate(2000));
        System.out.println(factorial.calculateFuture(2000));
    }

    private static class Calculation implements Runnable {

        // basically volatile isn't needed because of "piggybacking" on countdown latch,
        // but let's do it for better understanding.
        private volatile int factorialResponse;

        private final CountDownLatch countDownLatch;

        public Calculation(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        public int getFactorialResponse() {
            return factorialResponse;
        }

        @Override
        public void run() {
            BigInteger a = BigInteger.ONE;
            int i = 2;
            while (!Thread.currentThread().isInterrupted()) {
                a = a.multiply(BigInteger.valueOf(i++));
            }
            factorialResponse = i;
            countDownLatch.countDown();
        }
    }
}
