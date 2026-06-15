package com.minh.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory.Config;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GatewayRouteProperties.class)
public class GatewayRoutingConfig {

	private static final String BANKING_CORE_SERVICE_CB = "banking-core-service-cb";

	private static final String NOTIFICATION_SERVICE_CB = "notification-service-cb";

	private static final String FALLBACK_PREFIX = "forward:/internal/fallback/service-unavailable/";

	@Bean
	RouteLocator gatewayRoutes(RouteLocatorBuilder routeLocatorBuilder, GatewayRouteProperties routeProperties) {
		return routeLocatorBuilder.routes()
				.route("banking-core-service", predicate -> predicate
						.path(
								"/api/v1/accounts",
								"/api/v1/accounts/**",
								"/api/v1/transfers",
								"/api/v1/transfers/**",
								"/api/v1/transactions",
								"/api/v1/transactions/**",
								"/api/v1/outbox-events",
								"/api/v1/outbox-events/**",
								"/api/v1/audit-logs",
								"/api/v1/audit-logs/**")
						.filters(filters -> filters.circuitBreaker(configureCircuitBreaker(BANKING_CORE_SERVICE_CB, "banking-core-service")))
						.uri(routeProperties.getBankingCoreUri()))
				.route("notification-service", predicate -> predicate
						.path(
								"/api/v1/notifications",
								"/api/v1/notifications/**",
								"/api/v1/processed-events",
								"/api/v1/processed-events/**",
								"/api/v1/notification-service",
								"/api/v1/notification-service/**")
						.filters(filters -> filters.circuitBreaker(configureCircuitBreaker(NOTIFICATION_SERVICE_CB, "notification-service")))
						.uri(routeProperties.getNotificationServiceUri()))
				.build();
	}

	private java.util.function.Consumer<Config> configureCircuitBreaker(String circuitBreakerName, String fallbackService) {
		return config -> config.setName(circuitBreakerName).setFallbackUri(FALLBACK_PREFIX + fallbackService);
	}

}
