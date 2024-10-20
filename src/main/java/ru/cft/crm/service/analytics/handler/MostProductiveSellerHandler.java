package ru.cft.crm.service.analytics.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.cft.crm.dto.analitycs.MostProductiveSellerResponse;
import ru.cft.crm.dto.utilis.DateRange;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.exception.InvalidTimePeriodException;
import ru.cft.crm.exception.SellerNotFoundException;
import ru.cft.crm.mapper.SellerMapper;
import ru.cft.crm.repository.TransactionRepository;
import ru.cft.crm.type.TimePeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MostProductiveSellerHandler {
    private final TransactionRepository transactionRepository;
    private static final int ONE_DAY = 1;
    private static final int MONTHS_IN_QUARTER = 3;
    private static final int LAST_DAY_OFFSET = 1;
    private static final int ONE_YEAR = 1;

    public List<MostProductiveSellerResponse> getMostProductiveSellers(
            LocalDate date,
            String period,
            boolean active) {
        TimePeriod periodEnum = TimePeriod.getTimePeriod(period);
        DateRange dateRange = getDateRangeForPeriod(date, periodEnum);
        return getMostProductiveSellers(dateRange.start(), dateRange.end(), active);
    }

    private DateRange getDateRangeForPeriod(LocalDate date, TimePeriod period) {
        return switch (period) {
            case DAY -> getDateRangeForDay(date);
            case MONTH -> getDateRangeForMonth(date);
            case QUARTER -> getDateRangeForQuarter(date);
            case YEAR -> getDateRangeForYear(date);
            default -> throw new InvalidTimePeriodException("Неверный временной период. "
                    + "Доступные временные периоды: "
                    + "(" + TimePeriod.DAY + ", " + TimePeriod.MONTH + ", "
                    + TimePeriod.QUARTER + ", " + TimePeriod.YEAR + ")");
        };
    }

    private DateRange getDateRangeForDay(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(ONE_DAY).atStartOfDay();
        return new DateRange(startOfDay, endOfDay);
    }

    private DateRange getDateRangeForMonth(LocalDate date) {
        LocalDateTime startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth()).plusDays(ONE_DAY).atStartOfDay();
        return new DateRange(startOfMonth, endOfMonth);
    }

    private DateRange getDateRangeForQuarter(LocalDate date) {
        int quarter = (date.getMonthValue() - 1) / MONTHS_IN_QUARTER + 1;
        LocalDate startOfQuarter = LocalDate.of(date.getYear(), (quarter - 1) * MONTHS_IN_QUARTER + 1, 1);
        LocalDate endOfQuarter = startOfQuarter.plusMonths(MONTHS_IN_QUARTER).minusDays(1);
        LocalDateTime start = startOfQuarter.atStartOfDay();
        LocalDateTime end = endOfQuarter.plusDays(LAST_DAY_OFFSET).atStartOfDay();
        return new DateRange(start, end);
    }

    private DateRange getDateRangeForYear(LocalDate date) {
        LocalDateTime startOfYear = date.withDayOfYear(ONE_DAY).atStartOfDay();
        LocalDateTime endOfYear = date.plusYears(ONE_YEAR).withDayOfYear(ONE_DAY).atStartOfDay();
        return new DateRange(startOfYear, endOfYear);
    }

    private List<MostProductiveSellerResponse> getMostProductiveSellers(
            LocalDateTime start,
            LocalDateTime end,
            boolean active) {
        List<Transaction> transactions = getValidatedTransactions(start, end, active);
        Map<Seller, BigDecimal> sellerSums = compareSellersAndTransactions(transactions);

        BigDecimal maxSum = sellerSums.values().stream()
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new SellerNotFoundException("Ни одного продавца не найдено"));

        return sellerSums.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(maxSum) == 0)
                .map(entry -> SellerMapper.mapToMostProductiveSellerResponse(
                        entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<Transaction> getValidatedTransactions(LocalDateTime start, LocalDateTime end, boolean active) {
        List<Transaction> transactions = getTransactionsBetweenDates(start, end, active);
        if (transactions.isEmpty()) {
            throw new SellerNotFoundException("Ни одного продавца не найдено");
        }
        return transactions;
    }

    private List<Transaction> getTransactionsBetweenDates(
            LocalDateTime start,
            LocalDateTime end,
            boolean active) {
        if (active) {
            return transactionRepository
                    .findAllByTransactionDateBetweenAndSellerIsActiveTrue(start, end);
        } else {
            return transactionRepository
                    .findAllByTransactionDateBetweenAndSellerIsActiveFalse(start, end);
        }
    }

    private Map<Seller, BigDecimal> compareSellersAndTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(
                                Transaction::getSeller,
                                Collectors.mapping(
                                        Transaction::getAmount,
                                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                                )
                        )
                );
    }
}
