package ru.cft.crm.model.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

@Schema(description = "Запрос на обновление транзакции")
public record TransactionUpdateRequest(
        @Schema(description = "Сумма транзакции", example = "1500.50", minimum = "0.0", exclusiveMinimum = true)
        @DecimalMin(value = "0.0", inclusive = false, message = "Сумма должна быть положительной")
        BigDecimal amount,

        @Schema(description = "Тип оплаты", example = "CARD")
        String paymentType
) {}