package com.altoros.meetup.interruption;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Nikita Gorbachevski
 */
public class ExplicitFlag {

    public static class BrokenPrimeProducer extends Thread {

        private final BlockingQueue<BigInteger> queue;
        private volatile boolean cancelled = false;

        BrokenPrimeProducer(BlockingQueue<BigInteger> queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                BigInteger p = BigInteger.ONE;
                while (!cancelled) {
                    queue.put(p = p.nextProbablePrime());
                }
            } catch (InterruptedException consumed) {
                // exit
            }
        }

        public void cancel() {
            cancelled = true;
        }
    }

    void consumePrimes() throws InterruptedException {
        BlockingQueue<BigInteger> primes = new LinkedBlockingQueue<>(20);
        BrokenPrimeProducer producer = new BrokenPrimeProducer(primes);
        producer.start();
        try {
            while (needMorePrimes())
                consume(primes.take());
        } finally {
//            Thread.currentThread().interrupt();;
            producer.cancel();
        }
    }

    private boolean needMorePrimes() {
        return false;
    }

    private void consume(BigInteger value) {

    }
}
