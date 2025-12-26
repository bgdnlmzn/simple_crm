package ru.cft.crm.service.analytics;

import ru.cft.crm.model.analitycs.BestPeriodsResponse;
import ru.cft.crm.model.analitycs.MostProductiveSellerResponse;
import ru.cft.crm.model.analitycs.SellerWithTransactionsResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {
    BestPeriodsResponse getBestTransactionPeriod(Long sellerId);

    List<MostProductiveSellerResponse> getMostProductiveSellers(
            LocalDate date,
            String period,
            boolean active);

    List<SellerWithTransactionsResponse> getSellersWithTransactionsLessThan(
            BigDecimal maxAmount,
            LocalDate start,
            LocalDate end,
            boolean active);
}
