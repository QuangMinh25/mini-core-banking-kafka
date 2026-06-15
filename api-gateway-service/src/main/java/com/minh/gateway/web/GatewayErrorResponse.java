package com.minh.gateway.web;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GatewayErrorResponse(
		boolean success,
		String code,
		String message,
		String correlationId,
		String service) {

	public GatewayErrorResponse(boolean success, String code, String message, String correlationId) {
		this(success, code, message, correlationId, null);
	}

}
