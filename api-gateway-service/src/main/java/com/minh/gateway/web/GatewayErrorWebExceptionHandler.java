package com.minh.gateway.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.gateway.filter.CorrelationLoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Component
@Order(-2)
public class GatewayErrorWebExceptionHandler implements WebExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GatewayErrorWebExceptionHandler.class);

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		if (exchange.getResponse().isCommitted()) {
			return Mono.error(ex);
		}

		HttpStatusCode status = resolveStatus(ex);
		String correlationId = CorrelationLoggingFilter.getCorrelationId(exchange);
		GatewayErrorResponse response = buildResponse(status, correlationId);

		exchange.getResponse().setStatusCode(status);
		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
		exchange.getResponse().getHeaders().set(CorrelationLoggingFilter.CORRELATION_ID_HEADER, correlationId);

		log.warn(
				"Gateway error correlationId={} method={} path={} status={} errorType={}",
				correlationId,
				exchange.getRequest().getMethod(),
				exchange.getRequest().getPath().value(),
				status.value(),
				ex.getClass().getSimpleName()
		);

		DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(serialize(response));
		return exchange.getResponse().writeWith(Mono.just(buffer));
	}

	private HttpStatusCode resolveStatus(Throwable ex) {
		if (ex instanceof ResponseStatusException responseStatusException) {
			return responseStatusException.getStatusCode();
		}
		if (ex instanceof ConnectException || ex instanceof UnknownHostException || ex instanceof TimeoutException) {
			return HttpStatus.SERVICE_UNAVAILABLE;
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	private GatewayErrorResponse buildResponse(HttpStatusCode status, String correlationId) {
		if (status.value() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
			return new GatewayErrorResponse(
					false,
					"SERVICE_UNAVAILABLE",
					"Downstream service is temporarily unavailable",
					correlationId
			);
		}

		return new GatewayErrorResponse(
				false,
				"GATEWAY_ERROR",
				"Gateway request failed",
				correlationId
		);
	}

	private byte[] serialize(GatewayErrorResponse response) {
		try {
			return objectMapper.writeValueAsBytes(response);
		} catch (JsonProcessingException exception) {
			return "{\"success\":false,\"code\":\"GATEWAY_ERROR\",\"message\":\"Gateway request failed\"}"
					.getBytes(StandardCharsets.UTF_8);
		}
	}
}
