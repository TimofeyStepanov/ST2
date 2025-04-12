package ru.stepanoff.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.stepanoff.client.RedisClient;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisClientImpl implements RedisClient<String, List<String>> {
    private static final long MAX_TTL_MS = 100_000;

    private final RedisTemplate<String, List<String>> redisTemplate;

    @Override
    public void save(String key, List<String> value) {
        log.debug("Сохраняю в кеш ключ: {} и значение: {}", key, value);
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(MAX_TTL_MS));
    }

    @Override
    public Optional<List<String>> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }
}
