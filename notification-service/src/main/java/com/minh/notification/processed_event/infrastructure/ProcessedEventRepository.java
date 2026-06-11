package com.minh.notification.processed_event.infrastructure;

import com.minh.notification.processed_event.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, Long> {

	boolean existsByEventId(String eventId);
}
