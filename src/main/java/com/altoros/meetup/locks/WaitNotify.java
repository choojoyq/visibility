package com.altoros.meetup.locks;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nikita Gorbachevski
 */
public class WaitNotify {

    private boolean pizzaArrived = false;

    public void meetupInProgress() throws InterruptedException {
        synchronized (this) {
            while (!pizzaArrived) {
                System.out.println("time to sleep");
                wait();
            }
        }
        System.out.println("pizza time");
    }

    public void meetupFinished() {
        synchronized (this) {
            this.pizzaArrived = true;
            notifyAll();
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
