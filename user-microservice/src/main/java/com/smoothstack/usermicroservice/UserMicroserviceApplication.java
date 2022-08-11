package com.smoothstack.usermicroservice;

import com.smoothstack.common.configuration.AwsPinpointConfiguration;
import com.smoothstack.common.configuration.JwtConfiguration;
import com.smoothstack.common.services.JwtService;
import com.smoothstack.common.services.messaging.AwsPinpointService;
import com.smoothstack.common.services.messaging.MessagingService;
import com.smoothstack.common.services.messaging.MockMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@EntityScan("com.smoothstack")
@ComponentScan("com.smoothstack")
@EnableJpaRepositories("com.smoothstack")
@ConfigurationPropertiesScan("com.smoothstack.common.configuration")
@SpringBootApplication()
public class UserMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserMicroserviceApplication.class, args);
	}

	@Bean
	@Autowired
	public JwtService initJwtService(JwtConfiguration config) throws IOException {
		return new JwtService(config);
	}

	@Bean
	@Autowired
	@Profile("!test")
	public MessagingService initMsgService(AwsPinpointConfiguration config) {
		return new AwsPinpointService(config);
	}

	@Bean
	@Autowired
	@Profile("test")
	public MessagingService testMsgService() { return new MockMessagingService(); }
}
