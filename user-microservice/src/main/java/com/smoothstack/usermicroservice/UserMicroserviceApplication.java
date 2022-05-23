package com.smoothstack.usermicroservice;

import com.smoothstack.usermicroservice.service.messaging.MessagingService;
import com.smoothstack.usermicroservice.service.messaging.MockMessagingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.smoothstack")
@ComponentScan("com.smoothstack")
@EnableJpaRepositories("com.smoothstack")
@SpringBootApplication()
public class UserMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserMicroserviceApplication.class, args);
	}

	@Bean
	public MessagingService chooseMsgService() {
		// Edit this to change the messaging service
		return new MockMessagingService();
	}
}
