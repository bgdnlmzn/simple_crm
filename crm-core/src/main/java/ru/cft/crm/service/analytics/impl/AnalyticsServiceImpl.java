package ru.cft.crm.service.analytics.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.cft.crm.model.analitycs.BestPeriodsResponse;
import ru.cft.crm.model.analitycs.MostProductiveSellerResponse;
import ru.cft.crm.model.analitycs.SellerWithTransactionsResponse;
import ru.cft.crm.service.analytics.AnalyticsService;
import ru.cft.crm.service.analytics.handler.BestTransactionPeriodHandler;
import ru.cft.crm.service.analytics.handler.MostProductiveSellerHandler;
import ru.cft.crm.service.analytics.handler.TransactionsLessThenHandler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final BestTransactionPeriodHandler bestTransactionPeriodHandler;

    private final MostProductiveSellerHandler mostProductiveSellerHandler;

    private final TransactionsLessThenHandler transactionsLessThenHandler;

    @Override
    public BestPeriodsResponse getBestTransactionPeriod(Long sellerId) {
        return bestTransactionPeriodHandler.getBestTransactionPeriod(sellerId);
    }

    @Override
    public List<MostProductiveSellerResponse> getMostProductiveSellers(
            LocalDate date,
            String period,
            boolean active) {
        return mostProductiveSellerHandler.getMostProductiveSellers(date, period, active);
    }

    @Override
    public List<SellerWithTransactionsResponse> getSellersWithTransactionsLessThan(
            BigDecimal maxAmount,
            LocalDate start,
            LocalDate end,
            boolean active) {
        return transactionsLessThenHandler.getSellersWithTransactionsLessThan(
                maxAmount,
                start,
                end,
                active);
    }
}
