package com.minh.bankingcore.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class AuditService {

	private static final Logger log = LoggerFactory.getLogger(AuditService.class);
	private static final int MAX_ERROR_LENGTH = 1000;
	private static final int MAX_PAYLOAD_LENGTH = 2000;
	private static final Set<String> SENSITIVE_KEY_PATTERNS = Set.of(
			"password",
			"secret",
			"token",
			"authorization",
			"credential",
			"apikey",
			"certificate",
			"privatekey",
			"private_key",
			"private-key"
	);

	private final ObjectMapper objectMapper;
	private final AuditLogWriter auditLogWriter;

	public AuditService(ObjectMapper objectMapper, AuditLogWriter auditLogWriter) {
		this.objectMapper = objectMapper;
		this.auditLogWriter = auditLogWriter;
	}

	public void logSuccess(
			AuditAction action,
			String resourceType,
			String resourceId,
			Object requestData,
			Object responseData
	) {
		write(action, resourceType, resourceId, requestData, responseData, "SUCCESS", null);
	}

	public void logFailure(
			AuditAction action,
			String resourceType,
			String resourceId,
			Object requestData,
			Object responseData,
			Throwable throwable
	) {
		write(
				action,
				resourceType,
				resourceId,
				requestData,
				responseData,
				"FAILURE",
				throwable == null ? null : truncateError(throwable.getMessage())
		);
	}

	private void write(
			AuditAction action,
			String resourceType,
			String resourceId,
			Object requestData,
			Object responseData,
			String status,
			String errorMessage
	) {
		try {
			auditLogWriter.write(new AuditLogEntity(
					action.name(),
					resourceType,
					resourceId,
					toJson(requestData),
					toJson(responseData),
					status,
					errorMessage
			));
		} catch (RuntimeException exception) {
			log.warn("Failed to persist audit log for action={}", action, exception);
		}
	}

	private String toJson(Object data) {
		if (data == null) {
			return null;
		}

		try {
			JsonNode sanitized = sanitize(objectMapper.valueToTree(data));
			return truncatePayload(objectMapper.writeValueAsString(sanitized));
		} catch (IllegalArgumentException | JsonProcessingException exception) {
			log.warn("Failed to serialize audit payload", exception);
			return null;
		}
	}

	private JsonNode sanitize(JsonNode node) {
		if (node == null || node.isNull()) {
			return node;
		}

		if (node.isObject()) {
			ObjectNode copy = ((ObjectNode) node).deepCopy();
			Iterator<Map.Entry<String, JsonNode>> fields = copy.fields();
			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> field = fields.next();
				if (isSensitiveKey(field.getKey())) {
					copy.put(field.getKey(), "[REDACTED]");
				} else {
					copy.set(field.getKey(), sanitize(field.getValue()));
				}
			}
			return copy;
		}

		if (node.isArray()) {
			ArrayNode arrayNode = objectMapper.createArrayNode();
			for (JsonNode item : node) {
				arrayNode.add(sanitize(item));
			}
			return arrayNode;
		}

		return node;
	}

	private boolean isSensitiveKey(String key) {
		String normalized = key.toLowerCase(Locale.ROOT);
		for (String pattern : SENSITIVE_KEY_PATTERNS) {
			if (normalized.contains(pattern)) {
				return true;
			}
		}
		return false;
	}

	private String truncateError(String message) {
		if (message == null || message.length() <= MAX_ERROR_LENGTH) {
			return message;
		}
		return message.substring(0, MAX_ERROR_LENGTH);
	}

	private String truncatePayload(String payload) {
		if (payload == null || payload.length() <= MAX_PAYLOAD_LENGTH) {
			return payload;
		}
		return payload.substring(0, MAX_PAYLOAD_LENGTH - 3) + "...";
	}
}
