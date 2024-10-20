package ru.cft.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByIsActiveTrue();

    List<Transaction> findAllBySellerId(Long sellerId);

    Optional<Transaction> findByIdAndIsActive(Long id, Boolean isActive);

    List<Transaction> findBySellerAndIsActiveTrue(Seller seller);

    List<Transaction> findAllByTransactionDateBetweenAndSellerIsActiveTrue(
            LocalDateTime start,
            LocalDateTime end);

    List<Transaction> findAllByTransactionDateBetweenAndSellerIsActiveFalse(
            LocalDateTime start,
            LocalDateTime end);

}

