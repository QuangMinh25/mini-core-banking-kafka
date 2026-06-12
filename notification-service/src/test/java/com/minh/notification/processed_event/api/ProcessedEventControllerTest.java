package com.minh.notification.processed_event.api;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.minh.notification.common.api.GlobalExceptionHandler;
import com.minh.notification.common.api.PageResponse;
import com.minh.notification.processed_event.application.ProcessedEventService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ProcessedEventControllerTest {

	private MockMvc mockMvc;

	private ProcessedEventService processedEventService;

	@BeforeEach
	void setUp() {
		processedEventService = mock(ProcessedEventService.class);
		mockMvc = MockMvcBuilders.standaloneSetup(new ProcessedEventController(processedEventService))
				.setControllerAdvice(new GlobalExceptionHandler())
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Test
	void shouldReturnPaginatedProcessedEvents() throws Exception {
		ProcessedEventResponse response = new ProcessedEventResponse(
				"evt-001",
				"transaction.completed",
				LocalDateTime.of(2026, 6, 12, 10, 30).toString());
		PageResponse<ProcessedEventResponse> pageResponse = new PageResponse<>(List.of(response), 0, 20, 1, 1);

		when(processedEventService.getProcessedEvents(eq(0), eq(20), eq("transaction.completed"), eq("evt-001")))
				.thenReturn(pageResponse);

		mockMvc.perform(get("/api/v1/processed-events")
						.param("page", "0")
						.param("size", "20")
						.param("topic", "transaction.completed")
						.param("eventId", "evt-001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.content[0].eventId").value("evt-001"))
				.andExpect(jsonPath("$.data.content[0].topic").value("transaction.completed"));
	}

	@Test
	void shouldReturnProcessedEventByEventId() throws Exception {
		ProcessedEventResponse response = new ProcessedEventResponse(
				"evt-001",
				"transaction.completed",
				LocalDateTime.of(2026, 6, 12, 10, 30).toString());

		when(processedEventService.getProcessedEventByEventId("evt-001")).thenReturn(response);

		mockMvc.perform(get("/api/v1/processed-events/evt-001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.eventId").value("evt-001"));
	}
}
