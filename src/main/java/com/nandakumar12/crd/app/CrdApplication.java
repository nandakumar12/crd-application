package com.nandakumar12.crd.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication
@EnableAsync
public class CrdApplication {

	public static void main(String[] args) {
		 SpringApplication.run(CrdApplication.class, args);
	}


}
