package com.smoothstack.usermicroservice;

import com.smoothstack.usermicroservice.service.ConfigService;
import com.smoothstack.usermicroservice.service.messaging.AwsPinpointService;
import com.smoothstack.usermicroservice.service.messaging.MessagingService;
import com.smoothstack.usermicroservice.service.messaging.MockMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.smoothstack")
@ComponentScan("com.smoothstack")
@EnableJpaRepositories("com.smoothstack")
@EnableEurekaClient
@SpringBootApplication()
public class UserMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserMicroserviceApplication.class, args);
	}

	@Bean
	@Autowired
	public MessagingService chooseMsgService(ConfigService config) {
		// Edit this to change the messaging service
		return new MockMessagingService();
	}
}
