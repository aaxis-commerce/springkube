package com.aaxis.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:application.properties")
public class Application //implements CommandLineRunner 
{

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}