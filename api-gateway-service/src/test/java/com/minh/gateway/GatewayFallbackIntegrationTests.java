package com.minh.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayFallbackIntegrationTests {

	private static final int UNAVAILABLE_PORT = findAvailablePort();

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		String unavailableBaseUri = "http://127.0.0.1:" + UNAVAILABLE_PORT;
		registry.add("app.gateway.routes.banking-core-uri", () -> unavailableBaseUri);
		registry.add("app.gateway.routes.notification-service-uri", () -> unavailableBaseUri);
	}

	@LocalServerPort
	int port;

	@Test
	void returnsFallbackPayloadWhenDownstreamServiceIsUnavailable() {
		request("/api/v1/accounts")
				.header("X-Correlation-Id", "fallback-correlation-id")
				.exchange()
				.expectStatus()
				.isEqualTo(503)
				.expectHeader()
				.valueEquals("X-Correlation-Id", "fallback-correlation-id")
				.expectHeader()
				.contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.success").isEqualTo(false)
				.jsonPath("$.code").isEqualTo("SERVICE_UNAVAILABLE")
				.jsonPath("$.message").isEqualTo("Downstream service is temporarily unavailable")
				.jsonPath("$.service").isEqualTo("banking-core-service")
				.jsonPath("$.correlationId").isEqualTo("fallback-correlation-id");
	}

	@Test
	void returnsDownstreamDocsUnavailablePayloadWhenDocsProxyCannotReachService() {
		request("/v3/api-docs/core")
				.header("X-Correlation-Id", "docs-correlation-id")
				.exchange()
				.expectStatus()
				.isEqualTo(503)
				.expectHeader()
				.valueEquals("X-Correlation-Id", "docs-correlation-id")
				.expectHeader()
				.contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
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

	private static int findAvailablePort() {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			return serverSocket.getLocalPort();
		}
		catch (IOException ex) {
			throw new IllegalStateException("Unable to allocate an unavailable test port", ex);
		}
	}

}
