package com.minh.bankingcore.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuditServiceTest {

	@Test
	void logSuccessShouldRedactSensitiveFieldsAndTrimLargePayloads() {
		AuditLogWriter auditLogWriter = mock(AuditLogWriter.class);
		AuditService auditService = new AuditService(new ObjectMapper(), auditLogWriter);
		Map<String, Object> requestData = new LinkedHashMap<>();
		requestData.put("certificate", "cert-value");
		requestData.put("privateKey", "private-key-value");
		requestData.put("credentials", "credential-value");
		requestData.put("payload", "x".repeat(3000));

		auditService.logSuccess(
				AuditAction.PUBLISH_KAFKA_EVENT,
				"KAFKA_TOPIC",
				"TXN001",
				requestData,
				Map.of("status", "SUCCESS")
		);

		verify(auditLogWriter).write(argThat(entity -> {
			String serializedRequestData = readRequestData(entity);
			return serializedRequestData != null
					&& serializedRequestData.contains("\"certificate\":\"[REDACTED]\"")
					&& serializedRequestData.contains("\"privateKey\":\"[REDACTED]\"")
					&& serializedRequestData.contains("\"credentials\":\"[REDACTED]\"")
					&& serializedRequestData.endsWith("...");
		}));
	}

	private String readRequestData(AuditLogEntity entity) {
		try {
			var field = AuditLogEntity.class.getDeclaredField("requestData");
			field.setAccessible(true);
			return (String) field.get(entity);
		} catch (ReflectiveOperationException exception) {
			throw new AssertionError(exception);
		}
	}
}
