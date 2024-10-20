package ru.cft.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.cft.crm.dto.analitycs.BestPeriodsResponse;
import ru.cft.crm.dto.utilis.BestPeriod;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.exception.TransactionNotFoundException;
import ru.cft.crm.repository.TransactionRepository;
import ru.cft.crm.service.analytics.handler.BestTransactionPeriodHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayName("Тесты для BestTransactionPeriodHandler")
@SpringBootTest
public class BestTransactionPeriodHandlerTest {
    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private BestTransactionPeriodHandler bestTransactionPeriodHandler;

    private List<Transaction> transactions;

    @BeforeEach
    public void setUp() {
        transactions = List.of(
                createTransaction(
                        1L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 1, 1, 10, 0)),
                createTransaction(
                        2L,
                        BigDecimal.valueOf(200),
                        LocalDateTime.of(2024, 1, 1, 12, 0)),
                createTransaction(
                        3L,
                        BigDecimal.valueOf(300),
                        LocalDateTime.of(2024, 1, 2, 14, 0)),
                createTransaction(
                        4L,
                        BigDecimal.valueOf(400),
                        LocalDateTime.of(2024, 1, 2, 14, 0)),
                createTransaction(
                        5L, BigDecimal.valueOf(500),
                        LocalDateTime.of(2024, 1, 6, 15, 0)),
                createTransaction(
                        6L,
                        BigDecimal.valueOf(600),
                        LocalDateTime.of(2024, 2, 8, 15, 0)),
                createTransaction(
                        7L,
                        BigDecimal.valueOf(700),
                        LocalDateTime.of(2024, 2, 9, 16, 0)),
                createTransaction(
                        8L,
                        BigDecimal.valueOf(800),
                        LocalDateTime.of(2024, 2, 10, 17, 0)),
                createTransaction(
                        9L,
                        BigDecimal.valueOf(900),
                        LocalDateTime.of(2024, 2, 11, 18, 0)),
                createTransaction(
                        10L, BigDecimal.valueOf(1000),
                        LocalDateTime.of(2024, 2, 12, 19, 0)),
                createTransaction(
                        11L,
                        BigDecimal.valueOf(1100),
                        LocalDateTime.of(2024, 3, 1, 10, 0)),
                createTransaction(
                        12L,
                        BigDecimal.valueOf(1200),
                        LocalDateTime.of(2024, 3, 10, 12, 0)),
                createTransaction(
                        13L,
                        BigDecimal.valueOf(1300),
                        LocalDateTime.of(2024, 3, 15, 14, 0)),
                createTransaction(
                        14L,
                        BigDecimal.valueOf(1400),
                        LocalDateTime.of(2024, 3, 20, 16, 0)),
                createTransaction(
                        15L,
                        BigDecimal.valueOf(1500),
                        LocalDateTime.of(2024, 3, 30, 18, 0))
        );
    }

    private Transaction createTransaction(Long id, BigDecimal amount, LocalDateTime transactionDate) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(amount);
        transaction.setTransactionDate(transactionDate);
        transaction.setIsActive(true);
        return transaction;
    }

    @Test
    @DisplayName("Тест на правильность расчета количества транзакций за день")
    public void testBestDayPeriodCalculation() {
        Long sellerId = 1L;

        when(transactionRepository.findAllBySellerId(sellerId)).thenReturn(transactions);

        BestPeriodsResponse response = bestTransactionPeriodHandler.getBestTransactionPeriod(sellerId);

        BestPeriod bestDayPeriod = response.bestDayPeriod();

        assertThat(bestDayPeriod.transactionCount())
                .isEqualTo(2);
        assertThat(bestDayPeriod.startDate())
                .isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
        assertThat(bestDayPeriod.endDate())
                .isEqualTo(LocalDateTime.of(2024, 1, 1, 23, 59, 59));
    }

    @Test
    @DisplayName("Тест на правильность расчета количества транзакций за неделю")
    public void testBestWeekPeriodCalculation() {
        Long sellerId = 1L;

        when(transactionRepository.findAllBySellerId(sellerId)).thenReturn(transactions);

        BestPeriodsResponse response = bestTransactionPeriodHandler.getBestTransactionPeriod(sellerId);

        BestPeriod bestWeekPeriod = response.bestWeekPeriod();

        assertThat(bestWeekPeriod.transactionCount())
                .isEqualTo(5);
        assertThat(bestWeekPeriod.startDate())
                .isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
        assertThat(bestWeekPeriod.endDate())
                .isEqualTo(LocalDateTime.of(2024, 1, 7, 23, 59, 59));
    }

    @Test
    @DisplayName("Тест на правильность расчета количества транзакций за месяц")
    public void testBestMonthPeriodCalculation() {
        Long sellerId = 1L;

        when(transactionRepository.findAllBySellerId(sellerId)).thenReturn(transactions);

        BestPeriodsResponse response = bestTransactionPeriodHandler.getBestTransactionPeriod(sellerId);

        BestPeriod bestMonthPeriod = response.bestMonthPeriod();

        assertThat(bestMonthPeriod.transactionCount())
                .isEqualTo(5);
        assertThat(bestMonthPeriod.startDate())
                .isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
        assertThat(bestMonthPeriod.endDate())
                .isEqualTo(LocalDateTime.of(2024, 1, 31, 23, 59, 59));
    }

    @Test
    @DisplayName("Тест на получения периода, когда у продавца отсутствуют транзакции")
    public void testGetBestTransactionPeriodNoTransactions() {
        Long sellerId = 1L;

        when(transactionRepository.findAllBySellerId(sellerId)).thenReturn(List.of());

        assertThatThrownBy(() -> bestTransactionPeriodHandler.getBestTransactionPeriod(sellerId))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("У продавца нет транзакций");
    }
}
