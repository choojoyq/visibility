package com.altoros.meetup.interruption;

import java.util.concurrent.TimeUnit;

/**
 * @author Nikita Gorbachevski
 */
public class RepeatableTask implements Runnable {

    private final Runnable task;
    // nanos
    private final long repeatTimeout;

    // millis
    public RepeatableTask(Runnable task, long repeatTimeout) {
        this.task = task;
        this.repeatTimeout = TimeUnit.MILLISECONDS.toNanos(repeatTimeout);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    task.run();
                }
            });
            t.start();
            try {
                long start = System.nanoTime();
                t.join(10000);
                long duration = System.nanoTime() - start;
                if (duration < repeatTimeout) {
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(repeatTimeout - duration));
                }
            } catch (InterruptedException e) {
                // okay we were interrupted so next task shouldn't be started
                try {
                    t.join(10000);
                } catch (InterruptedException ex) {
                    t.interrupt();
                    return;
                }
                // now we can return safely as original task was finished
                // it's also possible to restore interrupted status
                return;
            }
        }
    }
}
