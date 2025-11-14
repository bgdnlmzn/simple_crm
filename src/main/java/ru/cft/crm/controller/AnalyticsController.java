package ru.cft.crm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cft.crm.controller.api.AnalyticsApi;
import ru.cft.crm.model.analitycs.BestPeriodsResponse;
import ru.cft.crm.model.analitycs.MostProductiveSellerResponse;
import ru.cft.crm.model.analitycs.SellerWithTransactionsResponse;
import ru.cft.crm.service.analytics.AnalyticsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/analytics")
@RequiredArgsConstructor
public class AnalyticsController implements AnalyticsApi {

    private final AnalyticsService analyticsService;

    @Override
    public List<MostProductiveSellerResponse> getMostProductiveSeller(
            LocalDate date,
            String period,
            Boolean active
    ) {
        return analyticsService.getMostProductiveSellers(
                date,
                period,
                active
        );
    }

    @Override
    public List<SellerWithTransactionsResponse> getSellersWithTransactionsLessThan(
            BigDecimal amount,
            LocalDate start,
            LocalDate end,
            Boolean active
    ) {
        return analyticsService.getSellersWithTransactionsLessThan(
                amount,
                start,
                end,
                active);
    }

    @Override
    public BestPeriodsResponse getBestTransactionPeriod(Long id) {
        return analyticsService.getBestTransactionPeriod(id);
    }
}
