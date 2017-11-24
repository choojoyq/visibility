package com.altoros.meetup.pool;

import java.util.concurrent.Executor;

/**
 * @author Nikita Gorbachevski
 */
public class Execs {

    public static class ThreadPerTaskExecutor implements Executor {

        @Override
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }

    public class WithinThreadExecutor implements Executor {

        @Override
        public void execute(Runnable r) {
            r.run();
        }
    }

}
