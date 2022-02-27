package com.example.kf.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Application {
	@Autowired
	ApplicationContext context;


	@PostConstruct
	public void init(){
		StreamsApp streamingApplication = context.getBean(StreamsApp.class);
		CustomerRepository customerRepository = context.getBean(CustomerRepository.class);
		streamingApplication.setCustomerRepository(customerRepository);
		streamingApplication.start();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
