package dev.honcharov.lab1;

import dev.honcharov.lab1.service.ParallelMergeSort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

@SpringBootApplication
public class Lab1Application {

    @Value("${server.port:8080}")
    private int port;

    public static void main(String[] args) {
        SpringApplication.run(Lab1Application.class, args);
    }

    @Bean
    public ParallelMergeSort parallelMergeSort() {
        return new ParallelMergeSort(new ForkJoinPool(), Runtime.getRuntime().availableProcessors());
    }

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public CommandLineRunner printStartupMessage() {
        return args -> {
            System.out.println();
            System.out.println("==========================================");
            System.out.println("Application started successfully");
            System.out.println("Frontend available at:");
            System.out.println("http://localhost:" + port + "/");
            System.out.println("==========================================");
            System.out.println();
        };
    }

}