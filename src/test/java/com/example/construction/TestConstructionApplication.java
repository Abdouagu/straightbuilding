package com.example.construction;

import org.springframework.boot.SpringApplication;

public class TestConstructionApplication {

    public static void main(String[] args) {
        SpringApplication.from(ConstructionApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
