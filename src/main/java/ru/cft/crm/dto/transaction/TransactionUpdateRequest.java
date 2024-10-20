package ru.cft.crm.dto.transaction;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record TransactionUpdateRequest(
        @DecimalMin(value = "0.0", inclusive = false, message = "Сумма должна быть положительной")
        BigDecimal amount,

        String paymentType
) {
}
