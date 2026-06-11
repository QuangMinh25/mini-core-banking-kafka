package com.minh.bankingcore.transaction;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TransactionReferenceGenerator {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private final AtomicInteger sequence = new AtomicInteger();
	private volatile String lastTimestamp = "";

	public synchronized String generate() {
		String timestamp = LocalDateTime.now().format(FORMATTER);

		if (!timestamp.equals(lastTimestamp)) {
			lastTimestamp = timestamp;
			sequence.set(0);
		}

		int currentSequence = sequence.incrementAndGet();
		return "TXN" + timestamp + "%03d".formatted(currentSequence);
	}
}
