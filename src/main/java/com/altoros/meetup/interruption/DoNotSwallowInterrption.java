package com.altoros.meetup.interruption;

import java.util.concurrent.BlockingQueue;

/**
 * @author Nikita Gorbachevski
 */
public class DoNotSwallowInterrption {

    public static class Task {

    }

    public class TaskRunnable implements Runnable {
        private final BlockingQueue<Task> queue;

        public TaskRunnable(BlockingQueue<Task> queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                processTask(queue.take());
            } catch (InterruptedException e) {
                // restore interrupted status
                Thread.currentThread().interrupt();
            }
        }

        private void processTask(Task task) {
        }
    }
}
