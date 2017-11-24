package com.altoros.meetup.blocks.collections;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Nikita Gorbachevski
 */
public class Cache<K, V> {

    private final Map<K, V> cache;
    private final Function<K, V> function;

    public Cache(Function<K, V> function) {
        this.function = function;
        this.cache = new ConcurrentHashMap<>();
    }

    public V compute(K key) {
        // atomic operation
        return cache.computeIfAbsent(key, function);
    }
}
