package com.zhimo.zhiyunpic.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-09-04 16:38
 * @Description
 */
@Configuration
public class CacheConfig {
    /**
     * 本地缓存实例
     * Key: 缓存的键，类型为 String
     * Value: 缓存的值，类型为 String
     */
    @Bean
    public Cache<String, String> buildCaffeine(){
        return Caffeine.newBuilder()
                // 设置缓存的初始容量为 1024
                .initialCapacity(1024)
                // 设置缓存的最大容量为 10000
                .maximumSize(10000L)
                // 设置写缓存后的过期时间：5 分钟
                .expireAfterWrite(5L, TimeUnit.MINUTES)
                // 构建并返回一个不包含自动加载功能的本地缓存对象
                .build();
    }

}
