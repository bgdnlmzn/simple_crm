package ru.cft.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cft.crm.entity.SellersHistory;

@Repository
public interface SellersHistoryRepository extends JpaRepository<SellersHistory, Long> {
}
