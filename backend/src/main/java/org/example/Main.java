package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.example", "controller", "service", "repository", "config"})
public class Main {
    public static void main(String[] args) {
        DatabaseFactory.initializeDatabase();
        DatabaseFactory.seedSampleData();
        SpringApplication.run(Main.class, args);
    }
}
