package com.abyss.l2cache;

import com.abyss.l2cache.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;

@SpringBootTest
class L2cacheApplicationTests {

    @Autowired
    private TestService testService;

    @Test
    void contextLoads() {
        for (int i=0;i<10;i++) {
            System.out.println(testService.test("test", String.valueOf(i)));
        }

        testService.clear();
    }

}
