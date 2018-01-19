package com.rbkmoney.magista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.rbkmoney.magista", "com.rbkmoney.dbinit"})
public class MagistaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagistaApplication.class, args);
    }
}
