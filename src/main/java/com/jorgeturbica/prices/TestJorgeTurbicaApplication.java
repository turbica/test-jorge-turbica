package com.jorgeturbica.prices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TestJorgeTurbicaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestJorgeTurbicaApplication.class, args);
    }

}
