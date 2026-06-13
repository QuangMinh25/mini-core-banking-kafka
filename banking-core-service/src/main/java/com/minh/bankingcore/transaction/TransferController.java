package com.minh.bankingcore.transaction;

import com.minh.bankingcore.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
@Tag(name = "Transfers", description = "Money transfer APIs.")
public class TransferController {

	static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

	private final TransferService transferService;

	public TransferController(TransferService transferService) {
		this.transferService = transferService;
	}

	@PostMapping
	@Operation(summary = "Transfer money", description = "Transfers funds between two accounts. Supports optional idempotency via request header.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid transfer request",
					content = @Content(
							examples = @ExampleObject(
									name = "Transfer request",
									value = """
											{
											  "fromAccountNo": "100001",
											  "toAccountNo": "100002",
											  "amount": 100000,
											  "currency": "VND"
											}
											"""
							)
					)
			)
	})
	public ApiResponse<TransferResponse> transfer(
			@Parameter(description = "Optional idempotency key to deduplicate retries.", example = "txn-100001-20260613-01")
			@RequestHeader(name = IDEMPOTENCY_KEY_HEADER, required = false) String idempotencyKey,
			@Valid @RequestBody TransferRequest request
	) {
		return ApiResponse.success(transferService.transfer(idempotencyKey, request));
	}
}
