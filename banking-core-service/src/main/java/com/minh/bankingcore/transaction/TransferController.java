package com.minh.bankingcore.transaction;

import com.minh.bankingcore.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

	private final TransferService transferService;

	public TransferController(TransferService transferService) {
		this.transferService = transferService;
	}

	@PostMapping
	public ApiResponse<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
		return ApiResponse.success(transferService.transfer(request));
	}
}
