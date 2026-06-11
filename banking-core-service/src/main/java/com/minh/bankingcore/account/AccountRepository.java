package com.minh.bankingcore.account;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

	boolean existsByAccountNo(String accountNo);

	Optional<AccountEntity> findByAccountNo(String accountNo);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from AccountEntity a where a.accountNo in :accountNos")
	List<AccountEntity> findAllByAccountNoInForUpdate(@Param("accountNos") Collection<String> accountNos);
}
