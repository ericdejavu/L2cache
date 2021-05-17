package com.abyss.l2cache.common.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author wujx37877
 * @Description //TODO
 * @Date 16:14 2021/5/13
 */
@ConfigurationProperties(prefix = "spring.cache.multi")
public class RedisEhcacheProperties {

    private Set<String> cacheNames = new HashSet<>();

    /** 是否存储空值，默认true，防止缓存穿透*/
    private boolean cacheNullValues = true;

    /** 是否动态根据cacheName创建Cache的实现，默认true*/
    private boolean dynamic = true;

    /** 缓存key的前缀*/
    private String cachePrefix;

    private Redis redis = new Redis();

    private Ehcache ehcache = new Ehcache();

    public boolean isCacheNullValues() {
        return cacheNullValues;
    }

    public class Redis {

        /** 全局过期时间，单位毫秒，默认不过期*/
        private long defaultExpiration = 0;

        /** 每个cacheName的过期时间，单位毫秒，优先级比defaultExpiration高*/
        private Map<String, Long> expires = new HashMap<>();

        /** 缓存更新时通知其他节点的topic名称*/
        private String topic = "cache:redis:ehcache:topic";

        public long getDefaultExpiration() {
            return defaultExpiration;
        }

        public void setDefaultExpiration(long defaultExpiration) {
            this.defaultExpiration = defaultExpiration;
        }

        public Map<String, Long> getExpires() {
            return expires;
        }

        public void setExpires(Map<String, Long> expires) {
            this.expires = expires;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        @Override
        public String toString() {
            return "Redis{" +
                    "defaultExpiration=" + defaultExpiration +
                    ", expires=" + expires +
                    ", topic='" + topic + '\'' +
                    '}';
        }
    }

    public class Ehcache {

        /**
         * 访问后过期时间，单位毫秒
         */
//        private long expireAfterAccess;

        /**
         * 写入后过期时间，单位毫秒
         */
        private long expireAfterWrite;


        /**
         * 每个ehcache最大缓存对象个数，超过此数量时按照失效策略（默认为LRU）
         */
        private long maxEntry = 500;

        public long getExpireAfterWrite() {
            return expireAfterWrite;
        }

        public void setExpireAfterWrite(long expireAfterWrite) {
            this.expireAfterWrite = expireAfterWrite;
        }

        public long getMaxEntry() {
            return maxEntry;
        }

        public void setMaxEntry(long maxEntry) {
            this.maxEntry = maxEntry;
        }

        @Override
        public String toString() {
            return "Ehcache{" +
                    "expireAfterWrite=" + expireAfterWrite +
                    ", maxEntry=" + maxEntry +
                    '}';
        }
    }

    public Set<String> getCacheNames() {
        return cacheNames;
    }

    public void setCacheNames(Set<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    public void setCacheNullValues(boolean cacheNullValues) {
        this.cacheNullValues = cacheNullValues;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getCachePrefix() {
        return cachePrefix;
    }

    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }

    public Ehcache getEhcache() {
        return ehcache;
    }

    public void setEhcache(Ehcache ehcache) {
        this.ehcache = ehcache;
    }

    @Override
    public String toString() {
        return "RedisEhcacheProperties{" +
                "cacheNames=" + cacheNames +
                ", cacheNullValues=" + cacheNullValues +
                ", dynamic=" + dynamic +
                ", cachePrefix='" + cachePrefix + '\'' +
                ", redis=" + redis +
                ", ehcache=" + ehcache +
                '}';
    }
}
