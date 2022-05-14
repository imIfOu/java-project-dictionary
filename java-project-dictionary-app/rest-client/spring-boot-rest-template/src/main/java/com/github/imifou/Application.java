package com.github.imifou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.github.imifou")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}