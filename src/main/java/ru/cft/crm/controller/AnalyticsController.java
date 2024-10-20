package ru.cft.crm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.cft.crm.dto.analitycs.BestPeriodsResponse;
import ru.cft.crm.dto.analitycs.MostProductiveSellerResponse;
import ru.cft.crm.dto.analitycs.SellerWithTransactionsResponse;
import ru.cft.crm.service.analytics.AnalyticsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/most-productive")
    public List<MostProductiveSellerResponse> getMostProductiveSeller(
            @RequestParam LocalDate date,
            @RequestParam String period,
            @RequestParam Boolean active) {
        return analyticsService.getMostProductiveSellers(
                date,
                period,
                active);
    }

    @GetMapping("/less-then")
    public List<SellerWithTransactionsResponse> getSellersWithTransactionsLessThan(
            @RequestParam BigDecimal amount,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam Boolean active) {
        return analyticsService.getSellersWithTransactionsLessThan(
                amount,
                start,
                end,
                active);
    }

    @GetMapping("/best-periods/seller/{id}")
    public BestPeriodsResponse getBestTransactionPeriod(
            @PathVariable Long id) {
        return analyticsService.getBestTransactionPeriod(id);
    }
}
