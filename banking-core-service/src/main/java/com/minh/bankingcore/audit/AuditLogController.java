package com.minh.bankingcore.audit;

import com.minh.bankingcore.common.ApiResponse;
import com.minh.bankingcore.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "Audit log query APIs.")
public class AuditLogController {

    private final AuditLogQueryService auditLogQueryService;

    public AuditLogController(AuditLogQueryService auditLogQueryService) {
        this.auditLogQueryService = auditLogQueryService;
    }

    @GetMapping
    @Operation(summary = "List audit logs", description = "Returns paginated audit logs with optional action, status, and date filters.")
    public ApiResponse<PageResponse<AuditLogResponse>> listAuditLogs(
            @Parameter(description = "Zero-based page index.", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size.", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Audit action filter.", example = "TRANSFER_CREATED")
            @RequestParam(required = false) String action,
            @Parameter(description = "Audit status filter.", example = "SUCCESS")
            @RequestParam(required = false) String status,
            @Parameter(description = "Filter audit logs from this date.", example = "2026-06-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter audit logs until this date.", example = "2026-06-13")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ApiResponse.success(auditLogQueryService.listAuditLogs(page, size, action, status, fromDate, toDate));
    }
}
