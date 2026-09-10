package com.nexamart.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NexaMartCustomerApplication {
  public static void main(String[] args) { SpringApplication.run(NexaMartCustomerApplication.class, args); }
}
