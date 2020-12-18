package com.example.userconsumer;

import com.example.userconsumer.controller.ConsumerControllerClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class UserConsumerApplication {

	public static void main(String[] args) throws IOException {
		ApplicationContext ctx = SpringApplication.run(
				UserConsumerApplication.class, args);

		ConsumerControllerClient consumerControllerClient = ctx.getBean(ConsumerControllerClient.class);
		System.out.println(consumerControllerClient);
		consumerControllerClient.getUser();
	}

	@Bean
	public ConsumerControllerClient consumerControllerClient() {
		return new ConsumerControllerClient();
	}
}
