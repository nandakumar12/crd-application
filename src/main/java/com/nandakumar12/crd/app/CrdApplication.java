package com.nandakumar12.crd.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication
@EnableAsync
public class CrdApplication {

	@Autowired
	ApplicationContext applicationContext;

	public static void main(String[] args) {
		 SpringApplication.run(CrdApplication.class, args);
	}


}
