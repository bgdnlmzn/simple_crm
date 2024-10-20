package ru.cft.crm.service.analytics.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.cft.crm.dto.analitycs.SellerWithTransactionsResponse;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.exception.InvalidStartDateException;
import ru.cft.crm.exception.SellerNotFoundException;
import ru.cft.crm.mapper.SellerMapper;
import ru.cft.crm.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionsLessThenHandler {
    private final TransactionRepository transactionRepository;
    private final static int ONE_DAY = 1;

    public List<SellerWithTransactionsResponse> getSellersWithTransactionsLessThan(
            BigDecimal maxAmount,
            LocalDate start,
            LocalDate end,
            boolean active) {
        validateDate(start, end);

        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.plusDays(ONE_DAY).atStartOfDay();

        List<Transaction> transactions = getValidatedTransactions(startTime, endTime, active);

        Map<Seller, BigDecimal> sellersSums = compareSellersAndTransactions(transactions);

        List<SellerWithTransactionsResponse> response = sellersSums.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(maxAmount) < 0)
                .map(entry -> SellerMapper.mapSellerWithTransactions(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
        if (response.isEmpty()) {
            throw new SellerNotFoundException(
                    "По данным параметрам ни одного пользователя не найдено");
        }

        return response;
    }

    private List<Transaction> getValidatedTransactions(
            LocalDateTime start,
            LocalDateTime end,
            boolean active) {
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

    private Map<Seller, BigDecimal> compareSellersAndTransactions(
            List<Transaction> transactions) {
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

    private void validateDate(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new InvalidStartDateException(
                    "Введенная начальная дата не должна быть позже конечной даты");
        }
    }
}
