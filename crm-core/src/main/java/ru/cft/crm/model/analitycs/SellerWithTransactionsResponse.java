package ru.cft.crm.model.analitycs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SellerWithTransactionsResponse(
        Long id,
        String sellerName,
        String contactInfo,
        BigDecimal transactionAmount,
        LocalDateTime registrationDate,
        LocalDateTime updatedAt,
        Boolean isActive
) {
}
