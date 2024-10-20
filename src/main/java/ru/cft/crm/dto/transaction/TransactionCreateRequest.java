package ru.cft.crm.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionCreateRequest(
        @NotNull(message = "ID продавца не должен быть пустым")
        Long sellerId,

        @NotNull(message = "Сумма не должна быть пустой.")
        @DecimalMin(value = "0.0", inclusive = false, message = "Сумма должна быть положительной")
        BigDecimal amount,

        @NotBlank(message = "Тип оплаты не должен быть пустым")
        String paymentType
) {
}
