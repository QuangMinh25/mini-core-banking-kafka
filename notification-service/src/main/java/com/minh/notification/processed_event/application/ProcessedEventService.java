package com.minh.notification.processed_event.application;

import com.minh.notification.processed_event.ProcessedEventEntity;
import com.minh.notification.processed_event.infrastructure.ProcessedEventRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessedEventService {

	private final ProcessedEventRepository processedEventRepository;

	public boolean isProcessed(String eventId) {
		return processedEventRepository.existsByEventId(eventId);
	}

	public boolean registerIfAbsent(String eventId, String topic) {
		try {
			processedEventRepository.save(ProcessedEventEntity.builder()
					.eventId(eventId)
					.topic(topic)
					.processedAt(LocalDateTime.now())
					.build());
			return true;
		} catch (DataIntegrityViolationException exception) {
			log.info("Processed event already exists for eventId={}", eventId);
			return false;
		}
	}
}
