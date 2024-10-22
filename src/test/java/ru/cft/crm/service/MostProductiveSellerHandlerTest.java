package ru.cft.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.cft.crm.dto.analitycs.MostProductiveSellerResponse;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.exception.InvalidTimePeriodException;
import ru.cft.crm.exception.SellerNotFoundException;
import ru.cft.crm.repository.TransactionRepository;
import ru.cft.crm.service.analytics.handler.MostProductiveSellerHandler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Тесты для MostProductiveSellerHandlerImpl")
@SpringBootTest
public class MostProductiveSellerHandlerTest {
    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private MostProductiveSellerHandler mostProductiveSellerHandler;

    private List<Transaction> transactionsForDay;
    private List<Transaction> transactionsForMonth;
    private List<Transaction> transactionsForQuarter;
    private List<Transaction> transactionsForYear;

    @BeforeEach
    public void setUp() {
        transactionsForDay = createTransactionDataForDay();
        transactionsForMonth = createTransactionDataForMonth();
        transactionsForQuarter = createTransactionDataForQuarter();
        transactionsForYear = createTransactionDataForYear();
    }

    private List<Transaction> createTransactionDataForYear() {
        Seller seller1 = createSeller(1L, "Seller 1", "seller1@mail.ru");
        Seller seller2 = createSeller(2L, "Seller 2", "seller2@mail.ru");

        return List.of(
                createTransaction(1L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        seller1),
                createTransaction(2L,
                        BigDecimal.valueOf(101),
                        LocalDateTime.of(2024, 11, 1, 10, 0),
                        seller1),
                createTransaction(3L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 5, 1, 10, 0),
                        seller2),
                createTransaction(4L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 12, 1, 10, 0),
                        seller2)
        );
    }

    private List<Transaction> createTransactionDataForQuarter() {
        Seller seller1 = createSeller(1L, "Seller 1", "seller1@mail.ru");
        Seller seller2 = createSeller(2L, "Seller 2", "seller2@mail.ru");

        return List.of(
                createTransaction(1L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        seller1),
                createTransaction(2L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 3, 1, 10, 0),
                        seller1),
                createTransaction(3L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 3, 1, 10, 0),
                        seller2),
                createTransaction(4L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 2, 1, 10, 0),
                        seller2)
        );
    }

    private List<Transaction> createTransactionDataForMonth() {
        Seller seller1 = createSeller(1L, "Seller 1", "seller1@mail.ru");
        Seller seller2 = createSeller(2L, "Seller 2", "seller2@mail.ru");
        Seller seller3 = createSeller(3L, "Seller 3", "seller3@mail.ru");

        return List.of(
                createTransaction(1L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        seller1),
                createTransaction(2L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 1, 3, 10, 0),
                        seller1),
                createTransaction(3L,
                        BigDecimal.valueOf(1000),
                        LocalDateTime.of(2024, 1, 12, 10, 0),
                        seller2),
                createTransaction(4L,
                        BigDecimal.valueOf(1000),
                        LocalDateTime.of(2024, 1, 26, 10, 0),
                        seller2),
                createTransaction(4L,
                        BigDecimal.valueOf(1100),
                        LocalDateTime.of(2024, 1, 24, 10, 0),
                        seller3),
                createTransaction(4L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 1, 30, 10, 0),
                        seller3)

        );
    }

    private List<Transaction> createTransactionDataForDay() {
        Seller seller1 = createSeller(1L, "Seller 1", "seller1@mail.ru");
        Seller seller2 = createSeller(2L, "Seller 2", "seller2@mail.ru");

        return List.of(
                createTransaction(1L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        seller1),
                createTransaction(2L,
                        BigDecimal.valueOf(100),
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        seller1),
                createTransaction(3L,
                        BigDecimal.valueOf(1000),
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        seller2),
                createTransaction(4L,
                        BigDecimal.valueOf(1000),
                        LocalDateTime.of(2024, 1, 1, 10, 0),
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
    @DisplayName("Тест на получение самого продуктивного продавца за день")
    public void testGetMostProductiveSellerForDay() {
        LocalDate date = LocalDate.of(2024, 1, 1);

        when(transactionRepository.findAllByTransactionDateBetweenAndSellerIsActiveTrue(any(), any()))
                .thenReturn(transactionsForDay);

        List<MostProductiveSellerResponse> result =
                mostProductiveSellerHandler.getMostProductiveSellers(date, "DAY", true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).sellerName()).isEqualTo("Seller 2");
        assertThat(result.get(0).amount()).isEqualTo(BigDecimal.valueOf(2000));
    }

    @Test
    @DisplayName("Тест на получение самого продуктивного продавца за месяц")
    public void testGetMostProductiveSellerForMonth() {
        LocalDate date = LocalDate.of(2024, 1, 1);

        when(transactionRepository.findAllByTransactionDateBetweenAndSellerIsActiveTrue(any(), any()))
                .thenReturn(transactionsForMonth);

        List<MostProductiveSellerResponse> result =
                mostProductiveSellerHandler.getMostProductiveSellers(date, "MONTH", true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).sellerName()).isEqualTo("Seller 2");
        assertThat(result.get(0).amount()).isEqualTo(BigDecimal.valueOf(2000));
    }

    @Test
    @DisplayName("Тест на получение самого продуктивного продавца за квартал")
    public void testGetMostProductiveSellerForQuarter() {
        LocalDate date = LocalDate.of(2024, 1, 1);

        when(transactionRepository.findAllByTransactionDateBetweenAndSellerIsActiveTrue(any(), any()))
                .thenReturn(transactionsForQuarter);

        List<MostProductiveSellerResponse> result =
                mostProductiveSellerHandler.getMostProductiveSellers(date, "QUARTER", true);

        assertThat(result).hasSize(2);
        assertThat(result.stream().map(MostProductiveSellerResponse::sellerName))
                .containsExactlyInAnyOrder("Seller 1", "Seller 2");
    }

    @Test
    @DisplayName("Тест на получение самого продуктивного продавца за год")
    public void testGetMostProductiveSellerForYear() {
        LocalDate date = LocalDate.of(2024, 1, 1);

        when(transactionRepository.findAllByTransactionDateBetweenAndSellerIsActiveTrue(any(), any()))
                .thenReturn(transactionsForYear);

        List<MostProductiveSellerResponse> result =
                mostProductiveSellerHandler.getMostProductiveSellers(date, "YEAR", true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).sellerName()).isEqualTo("Seller 1");
        assertThat(result.get(0).amount()).isEqualTo(BigDecimal.valueOf(201));
    }

    @Test
    @DisplayName("Тест на ошибку при отсутствии транзакций")
    public void testGetMostProductiveSellerNoTransactions() {
        LocalDate date = LocalDate.of(2023, 1, 1);

        when(transactionRepository.findAllByTransactionDateBetweenAndSellerIsActiveTrue(any(), any()))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> mostProductiveSellerHandler
                .getMostProductiveSellers(date, "YEAR", true))
                .isInstanceOf(SellerNotFoundException.class)
                .hasMessage("Ни одного продавца не найдено");
    }

    @Test
    @DisplayName("Тест на ошибку при неверном временном периоде")
    public void testGetMostProductiveSellerForYearNoTransactions() {
        LocalDate date = LocalDate.of(2024, 1, 1);

        when(transactionRepository.findAllByTransactionDateBetweenAndSellerIsActiveTrue(any(), any()))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> mostProductiveSellerHandler
                .getMostProductiveSellers(date, "TWODAYS", true))
                .isInstanceOf(InvalidTimePeriodException.class);
    }
}
