package com.shliffen.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.shliffen.backend.model")
@EntityScan("com.shliffen.backend.model")
public class BackendTask {

    public static void main(String[]args){
        SpringApplication.run(BackendTask.class,args);
    }

}
