package com.razor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class RawPublishedApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RawPublishedApplication.class, args);
    }
}
