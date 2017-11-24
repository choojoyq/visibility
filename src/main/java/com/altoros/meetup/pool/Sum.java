package com.altoros.meetup.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Nikita Gorbachevski
 */
public class Sum {

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Future<Integer> future = executorService.submit(() -> new Random().nextInt(100));
            futures.add(future);
        }
        int sum = 0;
        for (Future<Integer> future : futures) {
            sum += future.get();
        }
        System.out.println(sum);
        executorService.shutdown();
//        executorService.shutdownNow();
    }
}
