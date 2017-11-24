package com.altoros.meetup.blocks.collections;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

/**
 * @author Nikita Gorbachevski
 */
public class LoggingService {

    private final BlockingQueue<String> queue;
    private LoggerThread loggerThread;
    private final PrintWriter writer;

    public LoggingService(BlockingQueue<String> queue,
                          PrintWriter writer) {
        this.queue = queue;
        this.loggerThread = new LoggerThread();
        this.writer = writer;
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        loggerThread.interrupt();
    }

    public void log(String msg) throws InterruptedException {
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        String msg = queue.take();
                        writer.println(msg);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            } finally {
                writer.close();
            }
        }
    }
}
