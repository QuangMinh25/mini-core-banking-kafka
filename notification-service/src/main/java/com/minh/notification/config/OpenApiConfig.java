package com.minh.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI notificationOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Mini Core Banking - Notification API")
						.description("APIs for Kafka notification logs and processed event tracking.")
						.version("v1"));
	}
}
