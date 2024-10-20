package ru.cft.crm.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.cft.crm.type.ChangeType;
import ru.cft.crm.type.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transactions_history")
public class TransactionsHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long transactionId;

    private Long sellerId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private LocalDateTime transactionDate;

    private LocalDateTime updatedAt;

    private LocalDateTime changeTimestamp;

    @Enumerated(EnumType.STRING)
    private ChangeType changeType;
}