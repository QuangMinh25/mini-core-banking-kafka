package com.minh.notification.processed_event.infrastructure;

import com.minh.notification.processed_event.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, Long>,
		JpaSpecificationExecutor<ProcessedEventEntity> {

	boolean existsByEventId(String eventId);

	Optional<ProcessedEventEntity> findByEventId(String eventId);
}
