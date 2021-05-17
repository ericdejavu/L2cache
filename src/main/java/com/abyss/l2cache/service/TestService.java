package com.abyss.l2cache.service;

import com.abyss.l2cache.common.annotation.ClearCache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @Author wujx37877
 * @Description //TODO
 * @Date 14:00 2021/5/17
 */
@Service
public class TestService {

    @Cacheable(value = "test", key = "#key")
    public String test(String key, String value) {
        return value;
    }

    @ClearCache(value = "test")
    public void clear() {

    }

}
