package com.abyss.l2cache.common.cache;

import com.abyss.l2cache.model.CacheMessage;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author wujx37877
 * @Description //TODO
 * @Date 16:01 2021/5/13
 */
public class RedisEhcacheCache extends AbstractValueAdaptingCache {

    private RedisTemplate<Object, Object> redisTemplate;
    private Cache ehcache;

    private String name;
    private String cachePrefix;
    private long defaultExpiration = 0;
    private Map<String, Long> expires;
    private String topic = "cache:redis:caffeine:topic";

    public RedisEhcacheCache(String name, RedisTemplate<Object, Object> redisTemplate, Cache ehcache, RedisEhcacheProperties redisEhcacheProperties) {
        super(redisEhcacheProperties.isCacheNullValues());
        this.name = name;
        this.redisTemplate = redisTemplate;
        this.ehcache = ehcache;

        this.cachePrefix = redisEhcacheProperties.getCachePrefix();
        this.defaultExpiration = redisEhcacheProperties.getRedis().getDefaultExpiration();
        this.expires = redisEhcacheProperties.getRedis().getExpires();
        this.topic = redisEhcacheProperties.getRedis().getTopic();
    }

    /**
     * Create an {@code AbstractValueAdaptingCache} with the given setting.
     *
     * @param allowNullValues whether to allow for {@code null} values
     */
    protected RedisEhcacheCache(boolean allowNullValues) {
        super(allowNullValues);
    }

    @Override
    protected Object lookup(Object key) {
        Object cacheKey = getKey(key);
        Object value = ehcache.get(key);
        if(value != null) {
            return ((Element)value).getObjectValue();
        }

        value = redisTemplate.opsForValue().get(cacheKey);

        if(value != null) {
            ehcache.put(newElement(key, value));
        }
        return value;
    }

    private Object getKey(Object key) {
        return this.name.concat(":").concat(StringUtils.isEmpty(cachePrefix) ? key.toString() : cachePrefix.concat(":").concat(key.toString()));
    }

    private long getExpire() {
        long expire = defaultExpiration;
        Long cacheNameExpire = expires.get(this.name);
        return cacheNameExpire == null ? expire : cacheNameExpire.longValue();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = lookup(key);
        if(value != null) {
            return (T) value;
        }

        ReentrantLock lock = new ReentrantLock();
        try {
            lock.lock();
            value = lookup(key);
            if(value != null) {
                return (T) value;
            }
            value = valueLoader.call();
            Object storeValue = toStoreValue(valueLoader.call());
            put(key, storeValue);
            return (T) value;
        } catch (Exception e) {
            try {
                Class<?> c = Class.forName("org.springframework.cache.Cache$ValueRetrievalException");
                Constructor<?> constructor = c.getConstructor(Object.class, Callable.class, Throwable.class);
                RuntimeException exception = (RuntimeException) constructor.newInstance(key, valueLoader, e.getCause());
                throw exception;
            } catch (Exception e1) {
                throw new IllegalStateException(e1);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(Object key, Object value) {
        if (!super.isAllowNullValues() && value == null) {
            this.evict(key);
            return;
        }
        long expire = getExpire();
        if(expire > 0) {
            redisTemplate.opsForValue().set(getKey(key), toStoreValue(value), expire, TimeUnit.MILLISECONDS);
        } else {
            redisTemplate.opsForValue().set(getKey(key), toStoreValue(value));
        }

        push(new CacheMessage(this.name, key));

        ehcache.put(newElement(key, value));
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Object cacheKey = getKey(key);
        Object prevValue = null;
        // 考虑使用分布式锁，或者将redis的setIfAbsent改为原子性操作
        synchronized (key) {
            prevValue = redisTemplate.opsForValue().get(cacheKey);
            if(prevValue == null) {
                long expire = getExpire();
                if(expire > 0) {
                    redisTemplate.opsForValue().set(getKey(key), toStoreValue(value), expire, TimeUnit.MILLISECONDS);
                } else {
                    redisTemplate.opsForValue().set(getKey(key), toStoreValue(value));
                }

                push(new CacheMessage(this.name, key));

                ehcache.put(newElement(key, toStoreValue(value)));
            }
        }
        return toValueWrapper(prevValue);
    }

    @Override
    public void evict(Object key) {
        redisTemplate.delete(getKey(key));
        push(new CacheMessage(this.name, key));
        ehcache.remove(key);
    }

    @Override
    public void clear() {
        Set<Object> keys = redisTemplate.keys(this.name.concat(":*"));
        for(Object key : keys) {
            redisTemplate.delete(key);
        }

        push(new CacheMessage(this.name, null));

        ehcache.remove(this.name);
    }

    private void push(CacheMessage message) {
        redisTemplate.convertAndSend(topic, message);
    }

    private Element newElement(Object key, Object value) {
        return new Element(key, value);
    }
}
