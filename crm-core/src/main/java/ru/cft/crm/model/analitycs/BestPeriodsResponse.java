package ru.cft.crm.model.analitycs;

import ru.cft.crm.model.utilis.BestPeriod;

public record BestPeriodsResponse(
        BestPeriod bestDayPeriod,
        BestPeriod bestWeekPeriod,
        BestPeriod bestMonthPeriod
) {
}
