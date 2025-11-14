package com.job;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.job.mapper")
public class JobPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobPlatformApplication.class, args);
    }
} 