package com.abyss.l2cache.config;

import com.abyss.l2cache.common.cache.RedisEhcacheManager;
import com.abyss.l2cache.common.cache.RedisEhcacheProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
@AutoConfigureAfter(L2CacheAutoConfig.class)
@EnableConfigurationProperties(RedisEhcacheProperties.class)
@ConditionalOnProperty(name = "spring.cache.multi.useL2", havingValue = "true", matchIfMissing = false)
public class L2CacheAutoConfig {
    @Autowired
    private RedisEhcacheProperties redisEhcacheProperties;


    @Bean
    public RedisEhcacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate) {
        System.out.println("load cache config");
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        return new RedisEhcacheManager(redisEhcacheProperties, redisTemplate);
    }

}
