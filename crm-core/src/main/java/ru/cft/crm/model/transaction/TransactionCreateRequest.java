package ru.cft.crm.model.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Запрос на создание транзакции")
public record TransactionCreateRequest(
        @Schema(description = "ID продавца", example = "1")
        @NotNull(message = "ID продавца не должен быть пустым")
        Long sellerId,

        @Schema(description = "Сумма транзакции", example = "1500.50", minimum = "0.0", exclusiveMinimum = true)
        @NotNull(message = "Сумма не должна быть пустой.")
        @DecimalMin(value = "0.0", inclusive = false, message = "Сумма должна быть положительной")
        BigDecimal amount,

        @Schema(description = "Тип оплаты", example = "CASH")
        @NotBlank(message = "Тип оплаты не должен быть пустым")
        String paymentType
) {}
