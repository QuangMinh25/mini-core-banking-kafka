package com.minh.bankingcore.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 64)
	private String action;

	@Column(name = "resource_type", nullable = false, length = 64)
	private String resourceType;

	@Column(name = "resource_id", length = 128)
	private String resourceId;

	@Column(name = "request_data", columnDefinition = "TEXT")
	private String requestData;

	@Column(name = "response_data", columnDefinition = "TEXT")
	private String responseData;

	@Column(nullable = false, length = 32)
	private String status;

	@Column(name = "error_message", length = 1000)
	private String errorMessage;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	protected AuditLogEntity() {
	}

	public AuditLogEntity(
			String action,
			String resourceType,
			String resourceId,
			String requestData,
			String responseData,
			String status,
			String errorMessage
	) {
		this.action = action;
		this.resourceType = resourceType;
		this.resourceId = resourceId;
		this.requestData = requestData;
		this.responseData = responseData;
		this.status = status;
		this.errorMessage = errorMessage;
	}

	@PrePersist
	void onCreate() {
		this.createdAt = OffsetDateTime.now();
	}

	public String getAction() {
		return action;
	}

	public String getResourceType() {
		return resourceType;
	}

	public String getResourceId() {
		return resourceId;
	}

	public String getRequestData() {
		return requestData;
	}

	public String getResponseData() {
		return responseData;
	}

	public String getStatus() {
		return status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
}
