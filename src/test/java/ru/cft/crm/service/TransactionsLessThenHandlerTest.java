package ru.cft.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.cft.crm.dto.analitycs.SellerWithTransactionsResponse;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.exception.InvalidStartDateException;
import ru.cft.crm.exception.SellerNotFoundException;
import ru.cft.crm.repository.TransactionRepository;
import ru.cft.crm.service.analytics.handler.TransactionsLessThenHandler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Тесты для TransactionsLessThenHandler")
public class TransactionsLessThenHandlerTest {
    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionsLessThenHandler transactionsLessThenHandler;

    private List<Transaction> transactions;

    @BeforeEach
    public void setUp() {
        transactions = createTransactionData();
    }

    private List<Transaction> createTransactionData() {
        Seller seller1 = createSeller(1L, "Seller 1", "seller1@mail.ru");
        Seller seller2 = createSeller(2L, "Seller 2", "seller2@mail.ru");

        return List.of(
                createTransaction(1L,
                        BigDecimal.valueOf(50),
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        seller1),
                createTransaction(2L,
                        BigDecimal.valueOf(150),
                        LocalDateTime.of(2024, 1, 2, 11, 0),
                        seller1),
                createTransaction(3L,
                        BigDecimal.valueOf(80),
                        LocalDateTime.of(2024, 1, 3, 12, 0),
                        seller2),
                createTransaction(4L,
                        BigDecimal.valueOf(200),
                        LocalDateTime.of(2024, 1, 4, 13, 0),
                        seller2)
        );
    }

    private Transaction createTransaction(Long id, BigDecimal amount, LocalDateTime date, Seller seller) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(amount);
        transaction.setTransactionDate(date);
        transaction.setSeller(seller);
        transaction.setIsActive(true);
        return transaction;
    }

    private Seller createSeller(Long id, String sellerName, String contactInfo) {
        Seller seller = new Seller();
        seller.setId(id);
        seller.setSellerName(sellerName);
        seller.setContactInfo(contactInfo);
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setIsActive(true);
        return seller;
    }

    @Test
    @DisplayName("Тест на успешное получение продавцов с транзакциями меньше указанной суммы")
    public void testGetSellersWithTransactionsLessThanSuccess() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        BigDecimal maxAmount = BigDecimal.valueOf(270);

        when(transactionRepository.findAllByTransactionDateBetweenAndSellerIsActiveTrue(any(), any()))
                .thenReturn(transactions);

        List<SellerWithTransactionsResponse> result =
                transactionsLessThenHandler
                        .getSellersWithTransactionsLessThan(maxAmount, start, end, true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).sellerName()).isEqualTo("Seller 1");
        assertThat(result.get(0).transactionAmount()).isEqualTo(BigDecimal.valueOf(200));
    }

    @Test
    @DisplayName("Тест на отсутствие продавцов с транзакциями меньше указанной суммы")
    public void testGetSellersWithTransactionsLessThanNotFound() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        BigDecimal maxAmount = BigDecimal.valueOf(10);

        when(transactionRepository.findAllByTransactionDateBetweenAndSellerIsActiveTrue(any(), any()))
                .thenReturn(transactions);

        assertThatThrownBy(() ->
                transactionsLessThenHandler
                        .getSellersWithTransactionsLessThan(maxAmount, start, end, true))
                .isInstanceOf(SellerNotFoundException.class)
                .hasMessage("По данным параметрам ни одного пользователя не найдено");
    }

    @Test
    @DisplayName("Тест на ошибку при неверном диапазоне дат")
    public void testGetSellersWithTransactionsLessThanInvalidDateRange() {
        LocalDate start = LocalDate.of(2024, 2, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        assertThatThrownBy(() ->
                transactionsLessThenHandler
                        .getSellersWithTransactionsLessThan(BigDecimal.valueOf(100), start, end, true))
                .isInstanceOf(InvalidStartDateException.class)
                .hasMessage("Введенная начальная дата не должна быть позже конечной даты");
    }

    @Test
    @DisplayName("Тест на отсутствие транзакций в указанном диапазоне дат")
    public void testGetSellersWithTransactionsLessThanNoTransactionsFound() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        when(transactionRepository.findAllByTransactionDateBetweenAndSellerIsActiveTrue(any(), any()))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() ->
                transactionsLessThenHandler
                        .getSellersWithTransactionsLessThan(
                                BigDecimal.valueOf(100), start, end, true))
                .isInstanceOf(SellerNotFoundException.class)
                .hasMessage("Ни одного продавца не найдено");
    }
}
