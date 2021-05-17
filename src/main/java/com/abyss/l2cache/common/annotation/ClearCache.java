package com.abyss.l2cache.common.annotation;

import java.lang.annotation.*;

/**
 * @Author wujx37877
 * @Description 清除指定缓存
 * @Date 13:14 2021/5/17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClearCache {
    String[] value() default {};

    String condition() default "";

    boolean allEntries() default false;
}
