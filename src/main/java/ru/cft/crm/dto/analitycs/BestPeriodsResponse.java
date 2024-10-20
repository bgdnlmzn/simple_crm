package ru.cft.crm.dto.analitycs;

import ru.cft.crm.dto.utilis.BestPeriod;

public record BestPeriodsResponse(
        BestPeriod bestDayPeriod,
        BestPeriod bestWeekPeriod,
        BestPeriod bestMonthPeriod
) {
}
