package com.minh.bankingcore.account;

import org.springframework.data.domain.Page;

import java.util.List;

public record AccountStatementResponse(
		List<AccountStatementEntryResponse> entries,
		int page,
		int size,
		long totalElements,
		int totalPages
) {
	public static AccountStatementResponse from(Page<AccountStatementEntryResponse> page) {
		return new AccountStatementResponse(
				page.getContent(),
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages()
		);
	}
}
