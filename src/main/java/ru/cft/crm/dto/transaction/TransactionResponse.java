package ru.cft.crm.dto.transaction;

import ru.cft.crm.type.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long sellerId,
        BigDecimal amount,
        PaymentType paymentType,
        LocalDateTime transactionDate,
        LocalDateTime updatedAt,
        Boolean isActive
) {
}
