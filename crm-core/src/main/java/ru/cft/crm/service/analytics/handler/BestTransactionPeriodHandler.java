package ru.cft.crm.service.analytics.handler;

import ru.cft.crm.model.analitycs.BestPeriodsResponse;

public interface BestTransactionPeriodHandler {
    BestPeriodsResponse getBestTransactionPeriod(Long sellerId);
}
