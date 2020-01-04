package com.qingchao.disruptordemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;

@SpringBootApplication
public class DisruptorDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DisruptorDemoApplication.class, args);
	}

}
