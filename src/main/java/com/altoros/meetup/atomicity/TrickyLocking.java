package com.altoros.meetup.atomicity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Nikita Gorbachevski
 */
public class TrickyLocking {

    public class ListHelper<E> {
        public List<E> list = Collections.synchronizedList(new ArrayList<E>());

        public synchronized boolean putIfAbsent(E x) {
            boolean absent = !list.contains(x);
            if (absent)
                list.add(x);
            return absent;
        }
    }
}
