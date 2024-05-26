package com.aes.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CpsApp{

	public static void main(String[] args) {
		SpringApplication.run(CpsApp.class, args);
	}

}
