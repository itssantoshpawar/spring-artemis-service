package com.example.component2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class Component2Application {

    public static void main(String[] args) {
        SpringApplication.run(Component2Application.class, args);
    }
}
