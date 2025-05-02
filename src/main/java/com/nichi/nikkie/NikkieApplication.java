package com.nichi.nikkie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NikkieApplication {

	public static void main(String[] args) {
		if (args.length > 0) {
			System.setProperty("config.xml", args[0]);

		}


		SpringApplication.run(NikkieApplication.class, args);
	}
}
