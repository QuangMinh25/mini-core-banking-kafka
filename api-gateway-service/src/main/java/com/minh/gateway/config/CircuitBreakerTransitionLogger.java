package com.minh.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerTransitionLogger {

	private static final Logger log = LoggerFactory.getLogger(CircuitBreakerTransitionLogger.class);

	private static final List<String> CIRCUIT_BREAKER_NAMES = List.of(
			"banking-core-service-cb",
			"notification-service-cb"
	);

	private final CircuitBreakerRegistry circuitBreakerRegistry;

	public CircuitBreakerTransitionLogger(CircuitBreakerRegistry circuitBreakerRegistry) {
		this.circuitBreakerRegistry = circuitBreakerRegistry;
	}

	@PostConstruct
	void registerListeners() {
		for (String circuitBreakerName : CIRCUIT_BREAKER_NAMES) {
			circuitBreakerRegistry.circuitBreaker(circuitBreakerName)
					.getEventPublisher()
					.onStateTransition(event -> logTransition(circuitBreakerName, event));
		}
	}

	private void logTransition(String circuitBreakerName, CircuitBreakerOnStateTransitionEvent event) {
		log.info("Circuit breaker {} transitioned {} -> {}",
				circuitBreakerName,
				event.getStateTransition().getFromState(),
				event.getStateTransition().getToState());
	}

}
