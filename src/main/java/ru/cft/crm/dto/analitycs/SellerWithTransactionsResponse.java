package ru.cft.crm.dto.analitycs;

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
