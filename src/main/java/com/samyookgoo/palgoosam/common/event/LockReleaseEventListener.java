package com.samyookgoo.palgoosam.common.event;

import com.samyookgoo.palgoosam.common.lock.LockRetryHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LockReleaseEventListener {
    private final RedisMessageListenerContainer container;
    private final LockRetryHandler lockRetryHandler;
    private final static String LOCK_RELEASE_CHANNEL = "lock.release";

    @PostConstruct
    public void init() {
        container.addMessageListener((message, pattern) -> {
            String lockKey = message.toString();
            lockRetryHandler.retry(lockKey);
        }, new ChannelTopic(LOCK_RELEASE_CHANNEL));
    }
}