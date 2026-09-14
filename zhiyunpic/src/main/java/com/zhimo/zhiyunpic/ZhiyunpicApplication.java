package com.zhimo.zhiyunpic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 */
@EnableAsync
@SpringBootApplication
@MapperScan("com.zhimo.zhiyunpic.mapper")
public class ZhiyunpicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhiyunpicApplication.class, args);
    }

}
