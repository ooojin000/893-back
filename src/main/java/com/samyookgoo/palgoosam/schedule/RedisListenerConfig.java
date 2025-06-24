package com.samyookgoo.palgoosam.schedule;

import com.samyookgoo.palgoosam.auction.service.AuctionStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisListenerConfig {
    private final RedisConnectionFactory connectionFactory;
    private final AuctionStatusService auctionStatusService;
    private final AuctionStatusPublisher statusPublisher;
    private final AuctionStatusSubscriber statusSubscriber;
    private final ChannelTopic auctionStatusTopic;

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        MessageListenerAdapter adapter = new MessageListenerAdapter(statusSubscriber, "onMessage");

        adapter.setSerializer(new GenericJackson2JsonRedisSerializer());
        container.addMessageListener(adapter, auctionStatusTopic);

        return container;
    }

    @Bean
    public RedisKeyExpirationListener rediskeyExpirationListener(RedisMessageListenerContainer container) {
        RedisKeyExpirationListener listener =
                new RedisKeyExpirationListener(container, auctionStatusService, statusPublisher);

        container.addMessageListener(listener, new PatternTopic("__keyevent@*__:expired"));
        return listener;
    }
}
