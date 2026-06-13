package com.minh.gateway.web;

import com.minh.gateway.config.GatewayRouteProperties;
import com.minh.gateway.filter.CorrelationLoggingFilter;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Gateway Operations", description = "Gateway-owned fallback and documentation endpoints.")
public class OpenApiDocsProxyController {

	private static final Logger log = LoggerFactory.getLogger(OpenApiDocsProxyController.class);
	private static final String DOWNSTREAM_DOCS_PATH = "/v3/api-docs";

	private final WebClient.Builder webClientBuilder;
	private final GatewayRouteProperties properties;

	public OpenApiDocsProxyController(WebClient.Builder webClientBuilder, GatewayRouteProperties properties) {
		this.webClientBuilder = webClientBuilder;
		this.properties = properties;
	}

	@GetMapping(value = "/v3/api-docs/core", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Proxy Banking Core Service OpenAPI JSON through the gateway",
			description = "Fetches Banking Core Service OpenAPI JSON using a gateway-relative endpoint so consumers only depend on the gateway host.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Downstream OpenAPI JSON returned"),
					@ApiResponse(
							responseCode = "503",
							description = "Downstream docs unavailable",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = GatewayErrorResponse.class))
					)
			}
	)
	public Mono<ResponseEntity<Object>> bankingCoreDocs(ServerWebExchange exchange) {
		return proxyDocs(exchange, "banking-core-service", properties.getBankingCoreUri());
	}

	@GetMapping(value = "/v3/api-docs/notification", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Proxy Notification Service OpenAPI JSON through the gateway",
			description = "Fetches Notification Service OpenAPI JSON using a gateway-relative endpoint so consumers only depend on the gateway host.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Downstream OpenAPI JSON returned"),
					@ApiResponse(
							responseCode = "503",
							description = "Downstream docs unavailable",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = GatewayErrorResponse.class))
					)
			}
	)
	public Mono<ResponseEntity<Object>> notificationDocs(ServerWebExchange exchange) {
		return proxyDocs(exchange, "notification-service", properties.getNotificationServiceUri());
	}

	@Hidden
	private Mono<ResponseEntity<Object>> proxyDocs(ServerWebExchange exchange, String serviceName, String baseUri) {
		String correlationId = CorrelationLoggingFilter.getCorrelationId(exchange);

		return webClientBuilder.build()
				.get()
				.uri(baseUri + DOWNSTREAM_DOCS_PATH)
				.header(CorrelationLoggingFilter.CORRELATION_ID_HEADER, correlationId)
				.retrieve()
				.bodyToMono(String.class)
				.map(body -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.header(CorrelationLoggingFilter.CORRELATION_ID_HEADER, correlationId)
						.body((Object) body))
				.onErrorResume(exception -> {
					log.warn(
							"Unable to fetch downstream OpenAPI docs correlationId={} service={} uri={}",
							correlationId,
							serviceName,
							baseUri + DOWNSTREAM_DOCS_PATH
					);

					GatewayErrorResponse response = new GatewayErrorResponse(
							false,
							"DOWNSTREAM_DOCS_UNAVAILABLE",
							"Unable to fetch OpenAPI docs from downstream service",
							correlationId,
							serviceName
					);

					return Mono.just(ResponseEntity.status(503)
							.contentType(MediaType.APPLICATION_JSON)
							.header(CorrelationLoggingFilter.CORRELATION_ID_HEADER, correlationId)
							.body((Object) response));
				});
	}
}
