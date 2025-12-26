package ru.cft.crm.service.analytics.handler;

import ru.cft.crm.model.analitycs.MostProductiveSellerResponse;

import java.time.LocalDate;
import java.util.List;

public interface MostProductiveSellerHandler {
    List<MostProductiveSellerResponse> getMostProductiveSellers(
            LocalDate date,
            String period,
            boolean active);
}
