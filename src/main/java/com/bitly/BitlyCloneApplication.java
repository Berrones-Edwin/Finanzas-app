package com.bitly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BitlyCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(BitlyCloneApplication.class, args);
	}

}
