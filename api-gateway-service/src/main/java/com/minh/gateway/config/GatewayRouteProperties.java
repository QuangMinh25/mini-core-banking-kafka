package com.minh.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.gateway.routes")
public class GatewayRouteProperties {

	private String bankingCoreUri = "http://localhost:8081";
	private String notificationServiceUri = "http://localhost:8082";

	public String getBankingCoreUri() {
		return bankingCoreUri;
	}

	public void setBankingCoreUri(String bankingCoreUri) {
		this.bankingCoreUri = bankingCoreUri;
	}

	public String getNotificationServiceUri() {
		return notificationServiceUri;
	}

	public void setNotificationServiceUri(String notificationServiceUri) {
		this.notificationServiceUri = notificationServiceUri;
	}
}
