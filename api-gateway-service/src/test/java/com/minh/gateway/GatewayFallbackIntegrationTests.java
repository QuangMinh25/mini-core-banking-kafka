package com.minh.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"app.gateway.routes.banking-core-uri=http://127.0.0.1:65534",
				"app.gateway.routes.notification-service-uri=http://127.0.0.1:65534"
		}
)
class GatewayFallbackIntegrationTests {

	@LocalServerPort
	int port;

	@Test
	void returnsFallbackPayloadWhenDownstreamServiceIsUnavailable() {
		request("/api/v1/accounts")
				.header("X-Correlation-Id", "fallback-correlation-id")
				.exchange()
				.expectStatus().isEqualTo(503)
				.expectHeader().valueEquals("X-Correlation-Id", "fallback-correlation-id")
				.expectBody()
				.jsonPath("$.success").isEqualTo(false)
				.jsonPath("$.code").isEqualTo("SERVICE_UNAVAILABLE")
				.jsonPath("$.message").isEqualTo("Downstream service is temporarily unavailable")
				.jsonPath("$.correlationId").isEqualTo("fallback-correlation-id")
				.jsonPath("$.service").doesNotExist();
	}

	@Test
	void returnsDownstreamDocsUnavailablePayloadWhenDocsProxyCannotReachService() {
		request("/v3/api-docs/core")
				.header("X-Correlation-Id", "docs-correlation-id")
				.exchange()
				.expectStatus().isEqualTo(503)
				.expectHeader().valueEquals("X-Correlation-Id", "docs-correlation-id")
				.expectBody()
				.jsonPath("$.success").isEqualTo(false)
				.jsonPath("$.code").isEqualTo("DOWNSTREAM_DOCS_UNAVAILABLE")
				.jsonPath("$.message").isEqualTo("Unable to fetch OpenAPI docs from downstream service")
				.jsonPath("$.service").isEqualTo("banking-core-service")
				.jsonPath("$.correlationId").isEqualTo("docs-correlation-id");
	}

	private WebTestClient.RequestHeadersSpec<?> request(String path) {
		return WebTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.build()
				.get()
				.uri(path);
	}
}
