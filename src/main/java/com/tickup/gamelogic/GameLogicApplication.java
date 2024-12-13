package com.tickup.gamelogic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.tickup.gamelogic")
public class GameLogicApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameLogicApplication.class, args);
	}

}
