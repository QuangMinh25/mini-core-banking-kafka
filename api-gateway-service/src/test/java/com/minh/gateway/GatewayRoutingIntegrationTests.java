package com.minh.gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayRoutingIntegrationTests {

	private static HttpServer downstreamServer;
	private static String downstreamBaseUri;

	@LocalServerPort
	int port;

	@AfterAll
	static void stopDownstreamServer() {
		if (downstreamServer != null) {
			downstreamServer.stop(0);
		}
	}

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) throws IOException {
		ensureDownstreamServerStarted();
		registry.add("app.gateway.routes.banking-core-uri", () -> downstreamBaseUri);
		registry.add("app.gateway.routes.notification-service-uri", () -> downstreamBaseUri);
	}

	@Test
	void generatesCorrelationIdWhenMissing() {
		var result = request("/api/v1/accounts")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().exists("X-Correlation-Id")
				.expectBody(String.class)
				.returnResult();

		String correlationId = result.getResponseHeaders().getFirst("X-Correlation-Id");
		assertThat(correlationId).isNotBlank();
		assertThat(result.getResponseBody()).contains("\"correlationId\":\"" + correlationId + "\"");
	}

	@Test
	void preservesProvidedCorrelationId() {
		String correlationId = "test-correlation-id";

		var result = request("/api/v1/notifications")
				.header("X-Correlation-Id", correlationId)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().valueEquals("X-Correlation-Id", correlationId)
				.expectBody(String.class)
				.returnResult();

		assertThat(result.getResponseBody()).contains("\"correlationId\":\"" + correlationId + "\"");
	}

	@Test
	void exposesRequestedActuatorEndpoints() {
		request("/actuator/health").exchange().expectStatus().isOk();
		request("/actuator/info").exchange().expectStatus().isOk();
		request("/actuator/gateway/routes").exchange().expectStatus().isOk();
	}

	@Test
	void exposesGatewayOpenApiJson() {
		request("/v3/api-docs")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith("application/json")
				.expectBody(String.class)
				.value(body -> {
					assertThat(body).contains("\"title\":\"Mini Core Banking - API Gateway\"");
					assertThat(body).contains("\"version\":\"v1\"");
				});
	}

	@Test
	void proxiesCoreAndNotificationOpenApiJsonThroughGateway() {
		request("/v3/api-docs/core")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith("application/json")
				.expectBody(String.class)
				.value(body -> assertThat(body).contains("\"openapi\":\"3.0.1\""));

		request("/v3/api-docs/notification")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith("application/json")
				.expectBody(String.class)
				.value(body -> assertThat(body).contains("\"openapi\":\"3.0.1\""));
	}

	@Test
	void exposesSwaggerUiConfigWithAllGatewayRelativeGroups() {
		request("/v3/api-docs/swagger-config")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith("application/json")
				.expectBody(String.class)
				.value(body -> {
					assertThat(body).contains("\"urls.primaryName\":\"API Gateway\"");
					assertThat(body).contains("\"name\":\"API Gateway\"");
					assertThat(body).contains("\"url\":\"/v3/api-docs\"");
					assertThat(body).contains("\"name\":\"Banking Core Service\"");
					assertThat(body).contains("\"url\":\"/v3/api-docs/core\"");
					assertThat(body).contains("\"name\":\"Notification Service\"");
					assertThat(body).contains("\"url\":\"/v3/api-docs/notification\"");
				});
	}

	private WebTestClient.RequestHeadersSpec<?> request(String path) {
		return WebTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.build()
				.get()
				.uri(path);
	}

	private static synchronized void ensureDownstreamServerStarted() throws IOException {
		if (downstreamServer != null) {
			return;
		}

		downstreamServer = HttpServer.create(new InetSocketAddress(0), 0);
		downstreamServer.createContext("/", GatewayRoutingIntegrationTests::handleRequest);
		downstreamServer.start();
		downstreamBaseUri = "http://localhost:" + downstreamServer.getAddress().getPort();
	}

	private static void handleRequest(HttpExchange exchange) throws IOException {
		String correlationId = exchange.getRequestHeaders().getFirst("X-Correlation-Id");
		String response;
		if ("/v3/api-docs".equals(exchange.getRequestURI().getPath())) {
			response = "{\"openapi\":\"3.0.1\",\"info\":{\"title\":\"Downstream Test API\"}}";
		} else {
			response = "{\"path\":\"" + exchange.getRequestURI().getPath() + "\",\"correlationId\":\"" + correlationId + "\"}";
		}
		byte[] body = response.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", "application/json");
		exchange.sendResponseHeaders(200, body.length);
		exchange.getResponseBody().write(body);
		exchange.close();
	}
}
