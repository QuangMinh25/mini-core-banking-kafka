package com.minh.notification.notification.api;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.minh.notification.common.api.GlobalExceptionHandler;
import com.minh.notification.common.api.PageResponse;
import com.minh.notification.common.api.ResourceNotFoundException;
import com.minh.notification.notification.application.NotificationService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class NotificationControllerTest {

	private MockMvc mockMvc;

	private NotificationService notificationService;

	@BeforeEach
	void setUp() {
		notificationService = mock(NotificationService.class);
		mockMvc = MockMvcBuilders.standaloneSetup(new NotificationController(notificationService))
				.setControllerAdvice(new GlobalExceptionHandler())
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Test
	void shouldReturnPaginatedNotifications() throws Exception {
		NotificationResponse response = new NotificationResponse(
				"evt-001",
				"TXN001",
				"100001",
				"100002",
				new BigDecimal("100000.00"),
				"VND",
				"Transfer completed",
				"CREATED",
				LocalDateTime.of(2026, 6, 12, 10, 30).toString());
		PageResponse<NotificationResponse> pageResponse = new PageResponse<>(List.of(response), 0, 20, 1, 1);

		when(notificationService.getNotifications(eq(0), eq(20), eq("CREATED"), eq("TXN001"), eq("evt-001")))
				.thenReturn(pageResponse);

		mockMvc.perform(get("/api/v1/notifications")
						.param("page", "0")
						.param("size", "20")
						.param("status", "CREATED")
						.param("referenceNo", "TXN001")
						.param("eventId", "evt-001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.content[0].eventId").value("evt-001"))
				.andExpect(jsonPath("$.data.content[0].referenceNo").value("TXN001"))
				.andExpect(jsonPath("$.data.totalElements").value(1));
	}

	@Test
	void shouldReturnNotificationByEventId() throws Exception {
		NotificationResponse response = new NotificationResponse(
				"evt-001",
				"TXN001",
				"100001",
				"100002",
				new BigDecimal("100000.00"),
				"VND",
				"Transfer completed",
				"CREATED",
				LocalDateTime.of(2026, 6, 12, 10, 30).toString());

		when(notificationService.getNotificationByEventId("evt-001")).thenReturn(response);

		mockMvc.perform(get("/api/v1/notifications/events/evt-001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.eventId").value("evt-001"));
	}

	@Test
	void shouldReturnNotFoundWhenNotificationDoesNotExist() throws Exception {
		when(notificationService.getNotificationByEventId("missing"))
				.thenThrow(new ResourceNotFoundException("Notification not found for eventId=missing"));

		mockMvc.perform(get("/api/v1/notifications/events/missing"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Notification not found for eventId=missing"));
	}
}
