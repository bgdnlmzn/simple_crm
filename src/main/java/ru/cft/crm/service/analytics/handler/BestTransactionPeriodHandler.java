package ru.cft.crm.service.analytics.handler;

import ru.cft.crm.dto.analitycs.BestPeriodsResponse;

public interface BestTransactionPeriodHandler {
    BestPeriodsResponse getBestTransactionPeriod(Long sellerId);
}
