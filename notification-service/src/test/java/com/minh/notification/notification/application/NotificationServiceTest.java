package com.minh.notification.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.minh.notification.notification.domain.NotificationLogEntity;
import com.minh.notification.notification.infrastructure.NotificationLogRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

class NotificationServiceTest {

	private NotificationLogRepository notificationLogRepository;

	private NotificationService notificationService;

	@BeforeEach
	void setUp() {
		notificationLogRepository = mock(NotificationLogRepository.class);
		notificationService = new NotificationService(notificationLogRepository);
	}

	@Test
	void shouldReturnNotificationsWhenFiltersAreMissing() {
		when(notificationLogRepository.findAll(
				org.mockito.ArgumentMatchers.<Specification<NotificationLogEntity>>any(),
				any(PageRequest.class)))
				.thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

		var response = notificationService.getNotifications(0, 20, "", "", "");

		assertThat(response.content()).isEmpty();
		assertThat(response.page()).isZero();
		assertThat(response.size()).isEqualTo(20);
		assertThat(response.totalElements()).isZero();
	}
}
