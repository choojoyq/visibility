package com.altoros.meetup.atomicity;

/**
 * @author Nikita Gorbachevski
 */
public class DoubleCheckLocking {

    public static class Singleton {

    }

    private static class SingletonHolder {
        final static Singleton singleton = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonHolder.singleton;
    }

    private volatile static Singleton instance;

    // check-then-act
//    public static Singleton getInstance() {
//        if (instance == null) {
//            synchronized (DoubleCheckLocking.class) {
//                if (instance == null) {
//                    instance = new Singleton();
//                }
//            }
//        }
//        return instance;
//    }
}
