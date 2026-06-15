package com.minh.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI gatewayOpenApi(GatewayRouteProperties routeProperties,
			@Value("${spring.application.name}") String serviceName,
			@Value("${server.port:8080}") int port) {
		String localGatewayUrl = "http://localhost:" + port;

		Map<String, String> downstreamServiceUrls = new LinkedHashMap<>();
		downstreamServiceUrls.put("banking-core-service", routeProperties.getBankingCoreUri());
		downstreamServiceUrls.put("notification-service", routeProperties.getNotificationServiceUri());

		Map<String, List<String>> routeGroups = new LinkedHashMap<>();
		routeGroups.put("API Gateway", List.of(
				"Gateway-owned endpoints",
				"Fallback endpoint for unavailable downstream services"));
		routeGroups.put("Banking Core Service", List.of(
				"Gateway path routes: /api/v1/accounts/**, /api/v1/transfers/**, /api/v1/transactions/**, /api/v1/outbox-events/**, /api/v1/audit-logs/**",
				"OpenAPI proxy: /v3/api-docs/core"));
		routeGroups.put("Notification Service", List.of(
				"Gateway path routes: /api/v1/notifications/**, /api/v1/processed-events/**, /api/v1/notification-service/**",
				"OpenAPI proxy: /v3/api-docs/notification"));

		Info info = new Info()
				.title("Mini Core Banking - API Gateway")
				.description("Central entry point for Mini Core Banking APIs. Routes requests to banking-core-service and notification-service.")
				.version("v1");
		info.addExtension("x-service-name", serviceName);
		info.addExtension("x-local-gateway-url", localGatewayUrl);
		info.addExtension("x-downstream-service-urls", downstreamServiceUrls);
		info.addExtension("x-route-groups", routeGroups);

		return new OpenAPI()
				.info(info)
				.servers(List.of(new Server().url(localGatewayUrl).description("Local gateway URL")));
	}

	@Bean
	WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}

}
