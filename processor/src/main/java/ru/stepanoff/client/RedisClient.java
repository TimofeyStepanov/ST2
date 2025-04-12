package ru.stepanoff.client;

import java.util.Optional;

public interface RedisClient<K, V> {
    void save(K key, V value);

    Optional<V> get(K key);
}
