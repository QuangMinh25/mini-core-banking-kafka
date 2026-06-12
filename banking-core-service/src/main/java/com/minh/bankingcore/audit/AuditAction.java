package com.minh.bankingcore.audit;

public enum AuditAction {
	CREATE_ACCOUNT,
	TRANSFER_MONEY,
	CREATE_OUTBOX_EVENT,
	PUBLISH_KAFKA_EVENT
}
