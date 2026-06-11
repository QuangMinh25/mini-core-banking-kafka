package com.minh.notification.notification.infrastructure;

import com.minh.notification.notification.domain.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {
}
