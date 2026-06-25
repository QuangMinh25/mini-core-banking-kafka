package com.minh.gateway.web;

import com.minh.gateway.filter.CorrelationLoggingFilter;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequestMapping("/internal/fallback/service-unavailable")
@Hidden
@Tag(name = "Gateway Operations", description = "Gateway-owned endpoints")
public class GatewayFallbackController {

	@Operation(
			summary = "Fallback response for unavailable downstream services",
			description = "Returns a consistent 503 response when a downstream service cannot be reached.")
	@RequestMapping("/{service}")
	public ResponseEntity<GatewayErrorResponse> serviceUnavailable(@PathVariable String service,
			ServerWebExchange exchange) {
		String correlationId = CorrelationLoggingFilter.getCorrelationId(exchange);
		GatewayErrorResponse response = new GatewayErrorResponse(false,
				"SERVICE_UNAVAILABLE",
				"Downstream service is temporarily unavailable",
				correlationId,
				service);
		return ResponseEntity.status(503)
				.contentType(MediaType.APPLICATION_JSON)
				.header(CorrelationLoggingFilter.CORRELATION_ID_HEADER, correlationId)
				.body(response);
	}

}
