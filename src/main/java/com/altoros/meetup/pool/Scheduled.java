package com.altoros.meetup.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Nikita Gorbachevski
 */
public class Scheduled {

    public static void main(String[] args) throws Exception {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        scheduledExecutorService.scheduleAtFixedRate(() -> System.out.println("Hello"), 0, 1, TimeUnit.SECONDS);
        Thread.sleep(5000);
        scheduledExecutorService.shutdown();
    }
}
