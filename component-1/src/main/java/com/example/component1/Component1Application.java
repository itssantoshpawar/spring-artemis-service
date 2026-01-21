package com.example.component1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class Component1Application {

    public static void main(String[] args) {
        SpringApplication.run(Component1Application.class, args);
    }
}
