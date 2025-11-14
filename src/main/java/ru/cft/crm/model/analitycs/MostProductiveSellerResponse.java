package ru.cft.crm.model.analitycs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MostProductiveSellerResponse(
        Long id,
        String sellerName,
        String contactInfo,
        BigDecimal amount,
        LocalDateTime registrationDate
) {
}
