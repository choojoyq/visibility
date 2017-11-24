package com.altoros.meetup.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Nikita Gorbachevski
 */
public class WaitNotifyLocks {

    private boolean pizzaArrived = false;
    private final Lock lock;
    private final Condition condition;

    public WaitNotifyLocks() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void meetupInProgress() throws InterruptedException {
        lock.lock();
        try {
            while (!pizzaArrived) {
                System.out.println("time to sleep");
                condition.await();
            }
        } finally {
            lock.unlock();
        }

        System.out.println("pizza time");
    }

    public void meetupFinished() {
        lock.lock();
        try {
            this.pizzaArrived = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        WaitNotify waitNotify = new WaitNotify();
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        waitNotify.meetupInProgress();
                    } catch (InterruptedException e) {

                    }
                }
            }).start();
        }
        Thread.sleep(2000L);
        System.out.println("Meetup finished");
        waitNotify.meetupFinished();
    }
}
