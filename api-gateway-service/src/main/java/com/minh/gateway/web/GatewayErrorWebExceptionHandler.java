package com.minh.gateway.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minh.gateway.filter.CorrelationLoggingFilter;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GatewayErrorWebExceptionHandler implements WebExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GatewayErrorWebExceptionHandler.class);

	private final ObjectMapper objectMapper;

	public GatewayErrorWebExceptionHandler() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		this.objectMapper.findAndRegisterModules();
	}

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpResponse response = exchange.getResponse();
		if (response.isCommitted()) {
			return Mono.error(ex);
		}

		HttpStatusCode statusCode = resolveStatus(ex);
		String correlationId = CorrelationLoggingFilter.getCorrelationId(exchange);
		GatewayErrorResponse body = buildResponse(statusCode, correlationId);

		response.setStatusCode(statusCode);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		response.getHeaders().set(CorrelationLoggingFilter.CORRELATION_ID_HEADER, correlationId);

		log.warn("Gateway error correlationId={} method={} path={} status={} errorType={}",
				correlationId,
				exchange.getRequest().getMethod(),
				exchange.getRequest().getPath().value(),
				statusCode.value(),
				ex.getClass().getSimpleName());

		return response.writeWith(Mono.just(response.bufferFactory().wrap(serialize(body))));
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

	private GatewayErrorResponse buildResponse(HttpStatusCode statusCode, String correlationId) {
		if (statusCode.value() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
			return new GatewayErrorResponse(false,
					"SERVICE_UNAVAILABLE",
					"Downstream service is temporarily unavailable",
					correlationId);
		}
		return new GatewayErrorResponse(false,
				"GATEWAY_ERROR",
				"Gateway request failed",
				correlationId);
	}

	private byte[] serialize(GatewayErrorResponse response) {
		try {
			return objectMapper.writeValueAsBytes(response);
		}
		catch (JsonProcessingException ex) {
			return "{\"success\":false,\"code\":\"GATEWAY_ERROR\",\"message\":\"Gateway request failed\"}"
					.getBytes(StandardCharsets.UTF_8);
		}
	}

}
