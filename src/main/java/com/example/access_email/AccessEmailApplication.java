package com.example.access_email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccessEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessEmailApplication.class, args);
	}

}
