package com.abyss.l2cache.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author wujx37877
 * @Description //TODO
 * @Date 10:48 2021/5/14
 */
@Configuration
@ConditionalOnProperty(name = "spring.cache.multi.useL2", havingValue = "false", matchIfMissing = true)
public class CustomCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(net.sf.ehcache.CacheManager.getInstance());
    }
}
