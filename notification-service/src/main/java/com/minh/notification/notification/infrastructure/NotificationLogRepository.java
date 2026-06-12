package com.minh.notification.notification.infrastructure;

import com.minh.notification.notification.domain.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long>,
		JpaSpecificationExecutor<NotificationLogEntity> {

	Optional<NotificationLogEntity> findFirstByEventIdOrderByCreatedAtDesc(String eventId);
}
