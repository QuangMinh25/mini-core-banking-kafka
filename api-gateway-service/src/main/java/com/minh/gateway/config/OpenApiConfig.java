package com.minh.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI gatewayOpenApi(
			GatewayRouteProperties properties,
			@Value("${spring.application.name}") String applicationName,
			@Value("${server.port:8080}") int serverPort
	) {
		String gatewayUrl = "http://localhost:" + serverPort;
		Map<String, Object> downstreamServices = new LinkedHashMap<>();
		downstreamServices.put("banking-core-service", properties.getBankingCoreUri());
		downstreamServices.put("notification-service", properties.getNotificationServiceUri());

		Map<String, Object> routeGroups = new LinkedHashMap<>();
		routeGroups.put("API Gateway", List.of(
				"Gateway-owned endpoints",
				"Fallback endpoint for unavailable downstream services"
		));
		routeGroups.put("Banking Core Service", List.of(
				"Gateway path routes: /api/v1/accounts/**, /api/v1/transfers/**, /api/v1/transactions/**, /api/v1/outbox-events/**, /api/v1/audit-logs/**",
				"OpenAPI proxy: /v3/api-docs/core"
		));
		routeGroups.put("Notification Service", List.of(
				"Gateway path routes: /api/v1/notifications/**, /api/v1/processed-events/**, /api/v1/notification-service/**",
				"OpenAPI proxy: /v3/api-docs/notification"
		));

		Info info = new Info()
				.title("Mini Core Banking - API Gateway")
				.description("Central entry point for Mini Core Banking APIs. Routes requests to banking-core-service and notification-service.")
				.version("v1");
		info.addExtension("x-service-name", applicationName);
		info.addExtension("x-local-gateway-url", gatewayUrl);
		info.addExtension("x-downstream-service-urls", downstreamServices);
		info.addExtension("x-route-groups", routeGroups);

		return new OpenAPI()
				.info(info)
				.servers(List.of(new Server()
						.url(gatewayUrl)
						.description("Local gateway URL")));
	}

	@Bean
	WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}
}
