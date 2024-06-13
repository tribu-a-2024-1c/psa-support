package com.edu.uba.support;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SpringBootApplication
public class SupportApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupportApplication.class, args);
	}

}
