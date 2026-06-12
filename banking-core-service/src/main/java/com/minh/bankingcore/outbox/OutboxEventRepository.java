package com.minh.bankingcore.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, Long>, JpaSpecificationExecutor<OutboxEventEntity> {

	Optional<OutboxEventEntity> findByEventId(String eventId);

	@Query("""
			select event
			from OutboxEventEntity event
			where event.status in :statuses
			and event.retryCount < :retryLimit
			order by event.createdAt asc
			""")
	List<OutboxEventEntity> findRetryableEvents(
			@Param("statuses") Collection<OutboxEventStatus> statuses,
			@Param("retryLimit") int retryLimit,
			Pageable pageable
	);
}
