package ru.cft.crm.service.analytics.handler;

import ru.cft.crm.model.analitycs.SellerWithTransactionsResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionsLessThenHandler {
    List<SellerWithTransactionsResponse> getSellersWithTransactionsLessThan(
            BigDecimal maxAmount,
            LocalDate start,
            LocalDate end,
            boolean active);
}
