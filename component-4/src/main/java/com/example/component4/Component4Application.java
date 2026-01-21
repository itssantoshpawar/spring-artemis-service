package com.example.component4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class Component4Application {

    public static void main(String[] args) {
        SpringApplication.run(Component4Application.class, args);
    }
}
