package com.github.venth.micrometer_appdynamics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

class Memoizer<T, U> {
    private final Map<T, U> cache = new ConcurrentHashMap<>();

    private Memoizer() {}
    private Function<T, U> memoize(final Function<T, U> function) {
        return input -> cache.computeIfAbsent(input, function);
    }

    public static <T, U> Function<T, U> of(final Function<T, U> function) {
        return new Memoizer<T, U>().memoize(function);
    }
}