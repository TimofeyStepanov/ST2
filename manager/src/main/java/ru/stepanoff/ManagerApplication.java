package ru.stepanoff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ManagerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ManagerApplication.class, args);
	}
}

//http://localhost:8081/swagger-ui/index.html
//http://localhost:8081/actuator

// http://manager:8081/swagger-ui/index.html#/manager-controller/echo