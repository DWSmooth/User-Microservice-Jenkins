package com.smoothstack.usermicroservice;

import com.smoothstack.common.services.JwtService;
import com.smoothstack.common.services.messaging.AwsPinpointService;
import com.smoothstack.common.services.messaging.MessagingService;
import com.smoothstack.common.services.messaging.MockMessagingService;
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
@SpringBootApplication()
public class UserMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserMicroserviceApplication.class, args);
	}

	@Bean
	@Autowired
	public MessagingService initMsgService() {
		// Edit this to change the messaging service
		return new AwsPinpointService();
	}

	@Bean
	@Autowired
	public JwtService initJwtService() {
		return new JwtService();
	}
}
