package com.minh.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;

@Component
class GatewayOpenApiAggregationCustomizer implements OpenApiCustomizer {

	private static final Logger log = LoggerFactory.getLogger(GatewayOpenApiAggregationCustomizer.class);

	private static final String DOWNSTREAM_DOCS_PATH = "/v3/api-docs";

	private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(2);

	private final GatewayRouteProperties routeProperties;

	private final ObjectMapper objectMapper;

	private final HttpClient httpClient;

	GatewayOpenApiAggregationCustomizer(GatewayRouteProperties routeProperties, ObjectMapper objectMapper) {
		this.routeProperties = routeProperties;
		this.objectMapper = objectMapper;
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(REQUEST_TIMEOUT)
				.build();
	}

	@Override
	public void customise(OpenAPI openApi) {
		mergeDownstreamOpenApi(openApi, "banking-core-service", routeProperties.getBankingCoreUri());
		mergeDownstreamOpenApi(openApi, "notification-service", routeProperties.getNotificationServiceUri());
	}

	private void mergeDownstreamOpenApi(OpenAPI gatewayOpenApi, String serviceName, String downstreamBaseUri) {
		try {
			OpenAPI downstreamOpenApi = fetchDownstreamOpenApi(downstreamBaseUri);
			mergePaths(gatewayOpenApi, downstreamOpenApi);
			mergeComponents(gatewayOpenApi, downstreamOpenApi);
		}
		catch (IOException | InterruptedException ex) {
			if (ex instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			log.warn("Unable to aggregate downstream OpenAPI docs service={} uri={}",
					serviceName,
					downstreamBaseUri + DOWNSTREAM_DOCS_PATH);
		}
	}

	private OpenAPI fetchDownstreamOpenApi(String downstreamBaseUri) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder(URI.create(downstreamBaseUri + DOWNSTREAM_DOCS_PATH))
				.timeout(REQUEST_TIMEOUT)
				.GET()
				.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() < 200 || response.statusCode() >= 300) {
			throw new IOException("Unexpected OpenAPI docs status: " + response.statusCode());
		}
		return objectMapper.readValue(response.body(), OpenAPI.class);
	}

	private void mergePaths(OpenAPI gatewayOpenApi, OpenAPI downstreamOpenApi) {
		if (downstreamOpenApi.getPaths() == null || downstreamOpenApi.getPaths().isEmpty()) {
			return;
		}
		if (gatewayOpenApi.getPaths() == null) {
			gatewayOpenApi.setPaths(new Paths());
		}
		downstreamOpenApi.getPaths().forEach(gatewayOpenApi.getPaths()::addPathItem);
	}

	private void mergeComponents(OpenAPI gatewayOpenApi, OpenAPI downstreamOpenApi) {
		if (downstreamOpenApi.getComponents() == null) {
			return;
		}
		if (gatewayOpenApi.getComponents() == null) {
			gatewayOpenApi.setComponents(new Components());
		}

		Components gatewayComponents = gatewayOpenApi.getComponents();
		Components downstreamComponents = downstreamOpenApi.getComponents();
		mergeMap(gatewayComponents.getSchemas(), downstreamComponents.getSchemas(), gatewayComponents::setSchemas);
		mergeMap(gatewayComponents.getResponses(), downstreamComponents.getResponses(), gatewayComponents::setResponses);
		mergeMap(gatewayComponents.getParameters(), downstreamComponents.getParameters(), gatewayComponents::setParameters);
		mergeMap(gatewayComponents.getSecuritySchemes(), downstreamComponents.getSecuritySchemes(), gatewayComponents::setSecuritySchemes);
		mergeMap(gatewayComponents.getRequestBodies(), downstreamComponents.getRequestBodies(), gatewayComponents::setRequestBodies);
		mergeMap(gatewayComponents.getHeaders(), downstreamComponents.getHeaders(), gatewayComponents::setHeaders);
		mergeMap(gatewayComponents.getExamples(), downstreamComponents.getExamples(), gatewayComponents::setExamples);
	}

	private <T> void mergeMap(Map<String, T> gatewayValues, Map<String, T> downstreamValues,
			java.util.function.Consumer<Map<String, T>> setter) {
		if (downstreamValues == null || downstreamValues.isEmpty()) {
			return;
		}
		if (gatewayValues == null) {
			setter.accept(downstreamValues);
			return;
		}
		downstreamValues.forEach(gatewayValues::putIfAbsent);
	}

}
