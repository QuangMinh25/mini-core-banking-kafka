package com.minh.notification.health.api;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.minh.notification.health.application.NotificationServiceHealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class NotificationServiceHealthControllerTest {

	private MockMvc mockMvc;

	private NotificationServiceHealthService notificationServiceHealthService;

	@BeforeEach
	void setUp() {
		notificationServiceHealthService = mock(NotificationServiceHealthService.class);
		mockMvc = MockMvcBuilders.standaloneSetup(
						new NotificationServiceHealthController(notificationServiceHealthService))
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Test
	void shouldReturnHealthPayload() throws Exception {
		when(notificationServiceHealthService.getHealth()).thenReturn(new NotificationServiceHealthResponse(
				"notification-service",
				"UP",
				"transaction.completed",
				"notification-service"));

		mockMvc.perform(get("/api/v1/notification-service/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.serviceName").value("notification-service"))
				.andExpect(jsonPath("$.data.kafkaTopic").value("transaction.completed"))
				.andExpect(jsonPath("$.data.consumerGroup").value("notification-service"));
	}
}
