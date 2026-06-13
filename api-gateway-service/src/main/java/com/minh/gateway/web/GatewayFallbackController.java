package com.minh.gateway.web;

import com.minh.gateway.filter.CorrelationLoggingFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
@Tag(name = "Gateway Operations", description = "Gateway-owned fallback and documentation endpoints.")
public class GatewayFallbackController {

	@Operation(
			summary = "Fallback response for unavailable downstream services",
			description = "Returns a standardized gateway error payload when a routed downstream service is unavailable.",
			responses = {
					@ApiResponse(
							responseCode = "503",
							description = "Downstream service unavailable",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = GatewayErrorResponse.class))
					)
			}
	)
	@RequestMapping("/internal/fallback/service-unavailable")
	public ResponseEntity<GatewayErrorResponse> serviceUnavailable(ServerWebExchange exchange) {
		String correlationId = CorrelationLoggingFilter.getCorrelationId(exchange);
		GatewayErrorResponse response = new GatewayErrorResponse(
				false,
				"SERVICE_UNAVAILABLE",
				"Downstream service is temporarily unavailable",
				correlationId
		);

		return ResponseEntity.status(503)
				.contentType(MediaType.APPLICATION_JSON)
				.header(CorrelationLoggingFilter.CORRELATION_ID_HEADER, correlationId)
				.body(response);
	}
}
