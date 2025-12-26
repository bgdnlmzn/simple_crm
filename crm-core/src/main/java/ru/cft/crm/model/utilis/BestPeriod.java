package ru.cft.crm.model.utilis;

import ru.cft.crm.type.TimePeriod;

import java.time.LocalDateTime;

public record BestPeriod(
        LocalDateTime startDate,
        LocalDateTime endDate,
        TimePeriod periodType,
        Long transactionCount
) {
}
