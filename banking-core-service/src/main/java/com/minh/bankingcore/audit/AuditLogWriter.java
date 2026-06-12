package com.minh.bankingcore.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogWriter {

	private final AuditLogRepository auditLogRepository;

	public AuditLogWriter(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void write(AuditLogEntity auditLogEntity) {
		auditLogRepository.save(auditLogEntity);
	}
}
