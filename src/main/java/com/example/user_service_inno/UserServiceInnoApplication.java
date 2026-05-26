package com.example.user_service_inno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class UserServiceInnoApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceInnoApplication.class, args);
	}

}
