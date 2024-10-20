package ru.cft.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cft.crm.entity.TransactionsHistory;

@Repository
public interface TransactionsHistoryRepository extends JpaRepository<TransactionsHistory, Long> {
}
