package com.smart.park;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartPark {

	public static void main(String[] args) {
		SpringApplication.run(SmartPark.class, args);
	}

}
