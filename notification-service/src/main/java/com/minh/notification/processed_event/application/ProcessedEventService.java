package com.minh.notification.processed_event.application;

import com.minh.notification.common.api.PageResponse;
import com.minh.notification.common.api.ResourceNotFoundException;
import com.minh.notification.processed_event.ProcessedEventEntity;
import com.minh.notification.processed_event.api.ProcessedEventResponse;
import com.minh.notification.processed_event.infrastructure.ProcessedEventRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

	public PageResponse<ProcessedEventResponse> getProcessedEvents(int page, int size, String topic, String eventId) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "processedAt"));
		return PageResponse.from(processedEventRepository.findAll(
				processedEventSpecification(topic, eventId), pageRequest)
				.map(ProcessedEventResponse::from));
	}

	public ProcessedEventResponse getProcessedEventByEventId(String eventId) {
		return processedEventRepository.findByEventId(eventId)
				.map(ProcessedEventResponse::from)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Processed event not found for eventId=%s".formatted(eventId)));
	}

	private Specification<ProcessedEventEntity> processedEventSpecification(String topic, String eventId) {
		return Specification.allOf(
				hasTopic(topic),
				hasEventId(eventId));
	}

	private Specification<ProcessedEventEntity> hasTopic(String topic) {
		if (!StringUtils.hasText(topic)) {
			return null;
		}

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("topic"), topic.trim());
	}

	private Specification<ProcessedEventEntity> hasEventId(String eventId) {
		if (!StringUtils.hasText(eventId)) {
			return null;
		}

		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("eventId"), eventId.trim());
	}
}
