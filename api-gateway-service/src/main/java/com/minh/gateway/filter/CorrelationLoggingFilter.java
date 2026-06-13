package com.minh.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationLoggingFilter implements GlobalFilter, Ordered {

	public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
	public static final String CORRELATION_ID_ATTRIBUTE = "gatewayCorrelationId";
	private static final Logger log = LoggerFactory.getLogger(CorrelationLoggingFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String correlationId = resolveCorrelationId(exchange);
		long startTime = System.currentTimeMillis();

		ServerHttpRequest request = exchange.getRequest()
				.mutate()
				.headers(headers -> headers.set(CORRELATION_ID_HEADER, correlationId))
				.build();

		ServerWebExchange mutatedExchange = exchange.mutate().request(request).build();
		mutatedExchange.getAttributes().put(CORRELATION_ID_ATTRIBUTE, correlationId);
		mutatedExchange.getResponse().beforeCommit(() -> {
			mutatedExchange.getResponse().getHeaders().set(CORRELATION_ID_HEADER, correlationId);
			return Mono.empty();
		});

		return chain.filter(mutatedExchange)
				.doFinally(signalType -> logRequest(mutatedExchange, correlationId, startTime));
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	public static String getCorrelationId(ServerWebExchange exchange) {
		Object correlationId = exchange.getAttribute(CORRELATION_ID_ATTRIBUTE);
		if (correlationId instanceof String value && !value.isBlank()) {
			return value;
		}
		return resolveCorrelationId(exchange);
	}

	private static String resolveCorrelationId(ServerWebExchange exchange) {
		String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
		if (correlationId == null || correlationId.isBlank()) {
			return UUID.randomUUID().toString();
		}
		return correlationId;
	}

	private void logRequest(ServerWebExchange exchange, String correlationId, long startTime) {
		Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
		HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
		long durationMs = System.currentTimeMillis() - startTime;
		String targetRoute = route == null ? "-" : route.getId() + " -> " + route.getUri();
		String message = "Gateway request correlationId={} method={} path={} targetRoute={} status={} durationMs={}";
		Object[] arguments = {
				correlationId,
				exchange.getRequest().getMethod(),
				exchange.getRequest().getPath().value(),
				targetRoute,
				statusCode == null ? "UNKNOWN" : statusCode.value(),
				durationMs
		};

		if (exchange.getRequest().getPath().value().startsWith("/actuator")) {
			log.debug(message, arguments);
			return;
		}

		log.info(message, arguments);
	}
}
