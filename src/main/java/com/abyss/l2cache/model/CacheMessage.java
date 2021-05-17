package com.abyss.l2cache.model;

import java.io.Serializable;

/**
 * @Author wujx37877
 * @Description //TODO
 * @Date 16:33 2021/5/13
 */
public class CacheMessage implements Serializable {
    private static final long serialVersionUID = 5987219310442078193L;

    private String cacheName;
    private Object key;
    public CacheMessage(String cacheName, Object key) {
        super();
        this.cacheName = cacheName;
        this.key = key;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }
}
