package ru.cft.crm.dto.utilis;

import ru.cft.crm.type.TimePeriod;

import java.time.Duration;

public record PeriodDefinition(
        Duration duration,
        TimePeriod timePeriod
) {
}
