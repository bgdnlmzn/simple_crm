package ru.cft.crm.service.analytics.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.cft.crm.model.analitycs.BestPeriodsResponse;
import ru.cft.crm.model.utilis.BestPeriod;
import ru.cft.crm.model.utilis.PeriodDefinition;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.exception.InvalidTimePeriodException;
import ru.cft.crm.exception.TransactionNotFoundException;
import ru.cft.crm.repository.TransactionRepository;
import ru.cft.crm.service.analytics.handler.BestTransactionPeriodHandler;
import ru.cft.crm.type.TimePeriod;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BestTransactionPeriodHandlerImpl implements BestTransactionPeriodHandler {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public BestPeriodsResponse getBestTransactionPeriod(Long sellerId) {
        List<Transaction> transactions = transactionRepository.findAllBySellerId(sellerId);

        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException("У продавца нет транзакций");
        }

        List<PeriodDefinition> periods = List.of(
                new PeriodDefinition(Duration.ofDays(1), TimePeriod.DAY),
                new PeriodDefinition(Duration.ofDays(7), TimePeriod.WEEK),
                new PeriodDefinition(Duration.ofDays(30), TimePeriod.MONTH)
        );

        BestPeriod bestDayPeriod = findBestPeriod(transactions, periods.get(0));
        BestPeriod bestWeekPeriod = findBestPeriod(transactions, periods.get(1));
        BestPeriod bestMonthPeriod = findBestPeriod(transactions, periods.get(2));

        return new BestPeriodsResponse(bestDayPeriod, bestWeekPeriod, bestMonthPeriod);
    }

    private BestPeriod findBestPeriod(List<Transaction> transactions, PeriodDefinition periodDefinition) {
        List<Transaction> sortedTransactions = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .toList();

        LocalDateTime bestStartDate = null;
        LocalDateTime bestEndDate = null;
        long maxTransactionsCount = 0;

        int windowStart = 0;

        for (int windowEnd = 0; windowEnd < sortedTransactions.size(); windowEnd++) {
            LocalDateTime windowStartDate = setStartOfPeriod(sortedTransactions.get(windowStart).getTransactionDate(), periodDefinition);
            LocalDateTime windowEndDate = setEndOfPeriod(windowStartDate, periodDefinition);

            while (windowEnd < sortedTransactions.size() &&
                    !sortedTransactions.get(windowEnd).getTransactionDate().isAfter(windowEndDate)) {
                windowEnd++;
            }

            long transactionsCount = windowEnd - windowStart;

            if (transactionsCount > maxTransactionsCount) {
                maxTransactionsCount = transactionsCount;
                bestStartDate = windowStartDate;
                bestEndDate = windowEndDate;
            }

            windowStart++;
        }

        return new BestPeriod(
                bestStartDate,
                bestEndDate,
                periodDefinition.timePeriod(),
                maxTransactionsCount
        );
    }

    private LocalDateTime setStartOfPeriod(LocalDateTime dateTime, PeriodDefinition periodDefinition) {
        return switch (periodDefinition.timePeriod()) {
            case DAY -> dateTime.toLocalDate().atStartOfDay();
            case WEEK -> dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
            case MONTH -> dateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
            default -> throw new InvalidTimePeriodException("Некорректный временной промежуток");
        };
    }

    private LocalDateTime setEndOfPeriod(LocalDateTime startDate, PeriodDefinition periodDefinition) {
        return switch (periodDefinition.timePeriod()) {
            case DAY -> startDate.plusDays(1).minusSeconds(1);
            case WEEK -> startDate.plusWeeks(1).minusSeconds(1);
            case MONTH -> startDate.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).minusSeconds(1);
            default -> throw new InvalidTimePeriodException("Некорректный временной промежуток");
        };
    }
}
