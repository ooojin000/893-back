package com.samyookgoo.palgoosam.common.lock;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Slf4j
public class RedisLock {
    private final RedisTemplate<String, String> redisTemplate;
    private final String key;
    private final String value;

    public RedisLock(RedisTemplate<String, String> redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.value = UUID.randomUUID().toString();
    }

    public boolean tryLock(Duration timeout, int price) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, timeout);
        log.info("락 획득 {}: {}, value:{}, price: {}", key, value, success, price);
        return Boolean.TRUE.equals(success);
    }

    public boolean tryLockWithSpin(Duration totalTimeout, int price, int spinIntervalMillis) {
        long deadline = System.currentTimeMillis() + totalTimeout.toMillis();

        while (System.currentTimeMillis() < deadline) {
            if (tryLock(Duration.ofMillis(spinIntervalMillis), price)) {
                return true;
            }

            try {
                Thread.sleep(spinIntervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return false;
    }

    public void unlock(int price) {
        String lua = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis.call('del', KEYS[1]) else return 0 end";

        redisTemplate.execute(
                new DefaultRedisScript<>(lua, Long.class),
                Collections.singletonList(key),
                value
        );

        log.info("락 해제: {}, value:{}, price: {}", key, value, price);
    }
}