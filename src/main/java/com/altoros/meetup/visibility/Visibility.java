package com.altoros.meetup.visibility;

/**
 * @author Nikita Gorbachevski
 */
public class Visibility {

    private static boolean flag = true;

    public static void main(String[] args) {
        new Thread(new Runnable() {
            public void run() {
                long counter = 0;
                while (flag) {
                    counter++;
                }
                System.out.println("Thread 1 finished. Counted up to " + counter);
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                // Sleep for a bit so that thread 1 has a chance to start
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                System.out.println("Thread 2 finishing");
                flag = false;
            }
        }).start();
    }
}
