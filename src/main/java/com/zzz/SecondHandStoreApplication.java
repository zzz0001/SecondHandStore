package com.zzz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class SecondHandStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondHandStoreApplication.class, args);
    }

}
