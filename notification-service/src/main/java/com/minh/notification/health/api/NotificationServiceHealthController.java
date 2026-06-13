package com.minh.notification.health.api;

import com.minh.notification.common.api.ApiResponse;
import com.minh.notification.health.application.NotificationServiceHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification-service")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Notification service health and debug APIs.")
public class NotificationServiceHealthController {

	private final NotificationServiceHealthService notificationServiceHealthService;

	@GetMapping("/health")
	@Operation(summary = "Get notification service health")
	public ApiResponse<NotificationServiceHealthResponse> getHealth() {
		return ApiResponse.success(notificationServiceHealthService.getHealth());
	}
}
