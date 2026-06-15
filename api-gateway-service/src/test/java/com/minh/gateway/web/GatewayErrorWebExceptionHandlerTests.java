package com.minh.gateway.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

class GatewayErrorWebExceptionHandlerTests {

	@Test
	void returnsJsonForUnexpectedGatewayErrors() {
		GatewayErrorWebExceptionHandler handler = new GatewayErrorWebExceptionHandler();
		MockServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/api/v1/accounts").build());
		exchange.getAttributes().put("gatewayCorrelationId", "error-correlation-id");

		handler.handle(exchange, new RuntimeException("boom")).block();

		assertThat(exchange.getResponse().getStatusCode()).isNotNull();
		assertThat(exchange.getResponse().getStatusCode().value()).isEqualTo(500);
		assertThat(exchange.getResponse().getHeaders().getFirst("X-Correlation-Id")).isEqualTo("error-correlation-id");
		assertThat(exchange.getResponse().getBodyAsString().block())
				.contains("\"success\":false")
				.contains("\"code\":\"GATEWAY_ERROR\"")
				.contains("\"message\":\"Gateway request failed\"")
				.contains("\"correlationId\":\"error-correlation-id\"")
				.doesNotContain("\"service\"");
	}

}
