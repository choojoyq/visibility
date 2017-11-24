package com.altoros.meetup.pool;

import java.util.concurrent.CompletableFuture;

/**
 * @author Nikita Gorbachevski
 */
public class ComFuture {

    public static void main(String[] args) throws Exception {
        CompletableFuture<Void> completableFuture = CompletableFuture.completedFuture(null);
        String[] values = new String[]{"a", "b", "c"};
        for (String value : values) {
            completableFuture
                    .thenRun(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                    })
                    .thenRun(() -> System.out.println(value));
        }
        completableFuture.get();
    }
}
