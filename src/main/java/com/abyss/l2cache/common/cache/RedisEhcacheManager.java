package com.abyss.l2cache.common.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author wujx37877
 * @Description //TODO
 * @Date 16:41 2021/5/13
 */
public class RedisEhcacheManager implements CacheManager {

    private Map<String, Cache> cacheMap = new ConcurrentHashMap<>();
    private RedisEhcacheProperties redisEhcacheProperties;
    private RedisTemplate<Object, Object> redisTemplate;
    private boolean dynamic = true;
    private Set<String> cacheNames;


    public RedisEhcacheManager(RedisEhcacheProperties redisEhcacheProperties,
                               RedisTemplate<Object, Object> redisTemplate) {
        super();
        this.redisEhcacheProperties = redisEhcacheProperties;
        this.redisTemplate = redisTemplate;
        this.dynamic = redisEhcacheProperties.isDynamic();
        this.cacheNames = redisEhcacheProperties.getCacheNames();
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = cacheMap.get(name);
        if(cache != null) {
            return cache;
        }
        if(!dynamic && !cacheNames.contains(name)) {
            return cache;
        }

        cache = new RedisEhcacheCache(name, redisTemplate, getEhcache(name), redisEhcacheProperties);
        Cache oldCache = cacheMap.putIfAbsent(name, cache);
        return oldCache == null ? cache : oldCache;
    }

    public net.sf.ehcache.Cache getEhcache(String name){
        net.sf.ehcache.CacheManager mgr = net.sf.ehcache.CacheManager.getInstance();
        return (net.sf.ehcache.Cache) mgr.getEhcache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheNames;
    }
}
