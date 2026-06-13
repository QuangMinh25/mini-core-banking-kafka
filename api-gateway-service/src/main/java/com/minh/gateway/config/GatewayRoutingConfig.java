package com.minh.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GatewayRouteProperties.class)
public class GatewayRoutingConfig {

	private static final String SERVICE_UNAVAILABLE_FALLBACK_PATH = "forward:/internal/fallback/service-unavailable";

	@Bean
	RouteLocator gatewayRoutes(RouteLocatorBuilder builder, GatewayRouteProperties properties) {
		return builder.routes()
				.route("banking-core-service", route -> route
						.path(
								"/api/v1/accounts", "/api/v1/accounts/**",
								"/api/v1/transfers", "/api/v1/transfers/**",
								"/api/v1/transactions", "/api/v1/transactions/**",
								"/api/v1/audit-logs", "/api/v1/audit-logs/**",
								"/api/v1/outbox-events", "/api/v1/outbox-events/**"
						)
						.filters(filters -> filters.circuitBreaker(config -> config
								.setName("bankingCoreCircuitBreaker")
								.setFallbackUri(SERVICE_UNAVAILABLE_FALLBACK_PATH)))
						.uri(properties.getBankingCoreUri()))
				.route("notification-service", route -> route
						.path(
								"/api/v1/notification-service", "/api/v1/notification-service/**",
								"/api/v1/notifications", "/api/v1/notifications/**",
								"/api/v1/processed-events", "/api/v1/processed-events/**"
						)
						.filters(filters -> filters.circuitBreaker(config -> config
								.setName("notificationServiceCircuitBreaker")
								.setFallbackUri(SERVICE_UNAVAILABLE_FALLBACK_PATH)))
						.uri(properties.getNotificationServiceUri()))
				.build();
	}
}
