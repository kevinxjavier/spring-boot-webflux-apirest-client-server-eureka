package com.kevinpina.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @EnabledEurekaClient // No need it, Auto Enabled with the dependency spring-cloud-starter-netflix-eureka-client
@SpringBootApplication
public class WebfluxClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxClientApplication.class, args);
	}

}
