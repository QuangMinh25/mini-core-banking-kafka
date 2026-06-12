package com.minh.bankingcore;

import com.minh.bankingcore.account.AccountRepository;
import com.minh.bankingcore.audit.AuditLogRepository;
import com.minh.bankingcore.idempotency.IdempotencyService;
import com.minh.bankingcore.kafka.TransactionEventProducer;
import com.minh.bankingcore.ledger.LedgerEntryRepository;
import com.minh.bankingcore.outbox.OutboxEventFactory;
import com.minh.bankingcore.outbox.OutboxEventRepository;
import com.minh.bankingcore.summary.DailyTransactionSummaryRepository;
import com.minh.bankingcore.transaction.TransactionReferenceGenerator;
import com.minh.bankingcore.transaction.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                + "org.springframework.boot.orm.jpa.autoconfigure.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration,"
                + "org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration"
})
class BankingCoreServiceApplicationTests {

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private TransactionRepository transactionRepository;

    @MockitoBean
    private LedgerEntryRepository ledgerEntryRepository;

    @MockitoBean
    private TransactionReferenceGenerator transactionReferenceGenerator;

    @MockitoBean
    private TransactionEventProducer transactionEventProducer;

    @MockitoBean
    private OutboxEventRepository outboxEventRepository;

    @MockitoBean
    private OutboxEventFactory outboxEventFactory;

    @MockitoBean
    private IdempotencyService idempotencyService;

    @MockitoBean
    private AuditLogRepository auditLogRepository;

    @MockitoBean
    private DailyTransactionSummaryRepository dailyTransactionSummaryRepository;

    @Test
    void contextLoads() {
    }
}
