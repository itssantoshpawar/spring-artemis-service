package com.example.component3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class Component3Application {

    public static void main(String[] args) {
        SpringApplication.run(Component3Application.class, args);
    }
}
