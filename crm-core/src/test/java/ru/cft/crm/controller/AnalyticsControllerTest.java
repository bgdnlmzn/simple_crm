package ru.cft.crm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.repository.SellerRepository;
import ru.cft.crm.repository.TransactionRepository;
import ru.cft.crm.type.PaymentType;
import ru.cft.crm.type.TimePeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DisplayName("Тесты AnalyticsController")
public class AnalyticsControllerTest {
    @SuppressWarnings("resource")
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("test_pass");

    @DynamicPropertySource
    private static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() {
        transactionRepository.deleteAll();
        sellerRepository.deleteAll();

        Seller seller1 = new Seller();
        seller1.setSellerName("Seller One");
        seller1.setContactInfo("seller1@mail.com");
        seller1.setRegistrationDate(LocalDateTime.now());
        seller1.setUpdatedAt(LocalDateTime.now());
        seller1.setIsActive(true);
        sellerRepository.save(seller1);

        Seller seller2 = new Seller();
        seller2.setSellerName("Seller Two");
        seller2.setContactInfo("seller2@mail.com");
        seller2.setRegistrationDate(LocalDateTime.now());
        seller2.setUpdatedAt(LocalDateTime.now());
        seller2.setIsActive(true);
        sellerRepository.save(seller2);

        Transaction transaction1 = new Transaction();
        transaction1.setSeller(seller1);
        transaction1.setAmount(new BigDecimal("500"));
        transaction1.setPaymentType(PaymentType.CARD);
        transaction1.setTransactionDate(LocalDateTime.of(2024,
                1,
                1,
                0,
                0)
        );
        transaction1.setUpdatedAt(LocalDateTime.now());
        transaction1.setIsActive(true);
        transactionRepository.save(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setSeller(seller1);
        transaction2.setAmount(new BigDecimal("1500"));
        transaction2.setPaymentType(PaymentType.CARD);
        transaction2.setTransactionDate(LocalDateTime.of(2024,
                1,
                1,
                0,
                0)
        );
        transaction2.setUpdatedAt(LocalDateTime.now());
        transaction2.setIsActive(true);
        transactionRepository.save(transaction2);

        Transaction transaction3 = new Transaction();
        transaction3.setSeller(seller2);
        transaction3.setAmount(new BigDecimal("900"));
        transaction3.setPaymentType(PaymentType.CASH);
        transaction3.setTransactionDate(LocalDateTime.of(2024,
                1,
                2,
                0,
                0)
        );
        transaction3.setUpdatedAt(LocalDateTime.now());
        transaction3.setIsActive(true);
        transactionRepository.save(transaction3);

        Transaction transaction4 = new Transaction();
        transaction4.setSeller(seller2);
        transaction4.setAmount(new BigDecimal("900"));
        transaction4.setPaymentType(PaymentType.CASH);
        transaction4.setTransactionDate(LocalDateTime.of(2024,
                1,
                2,
                0,
                0)
        );
        transaction4.setUpdatedAt(LocalDateTime.now());
        transaction4.setIsActive(true);
        transactionRepository.save(transaction4);

    }

    @Test
    @DisplayName("Тест на получение самого продуктивного продавца за месяц")
    public void testGetMostProductiveSellerPerMonth() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 1);
        String period = "MONTH";
        boolean active = true;

        mockMvc.perform(get("/api/analytics/most-productive")
                        .param("date", date.toString())
                        .param("period", period)
                        .param("active", String.valueOf(active))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].sellerName").value("Seller One"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на получение самого продуктивного продавца за день")
    public void testGetMostProductiveSellerPerDay() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 2);
        String period = "DAY";
        boolean active = true;

        mockMvc.perform(get("/api/analytics/most-productive")
                        .param("date", date.toString())
                        .param("period", period)
                        .param("active", String.valueOf(active))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].sellerName").value("Seller Two"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на получение самого продуктивного продавца за квартал")
    public void testGetMostProductiveSellerPerQuarter() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 1);
        String period = "QUARTER";
        boolean active = true;

        mockMvc.perform(get("/api/analytics/most-productive")
                        .param("date", date.toString())
                        .param("period", period)
                        .param("active", String.valueOf(active))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].sellerName").value("Seller One"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на получение самого продуктивного продавца за год")
    public void testGetMostProductiveSellerPerYear() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 1);
        String period = "YEAR";
        boolean active = true;

        mockMvc.perform(get("/api/analytics/most-productive")
                        .param("date", date.toString())
                        .param("period", period)
                        .param("active", String.valueOf(active))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].sellerName").value("Seller One"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на получение самого продуктивного продавца(некорректный параметр)")
    public void testGetMostProductiveSellerInvalidPeriod() throws Exception {
        LocalDate date = LocalDate.of(2024, 1, 1);
        String period = "WEEK";
        boolean active = true;

        mockMvc.perform(get("/api/analytics/most-productive")
                        .param("date", date.toString())
                        .param("period", period)
                        .param("active", String.valueOf(active))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Неверный временной период. "
                                + "Доступные временные периоды: "
                                + "(" + TimePeriod.DAY + ", " + TimePeriod.MONTH + ", "
                                + TimePeriod.QUARTER + ", " + TimePeriod.YEAR + ")"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на получение продавцов с транзакциями меньше amount")
    public void testGetSellersWithTransactionsLessThan() throws Exception {
        BigDecimal amount = new BigDecimal("1801");
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 2, 28);
        boolean active = true;

        mockMvc.perform(get("/api/analytics/less-then")
                        .param("amount", amount.toString())
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .param("active", Boolean.toString(active))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].sellerName").value("Seller Two"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на получение продавцов с транзакциями меньше amount(некорректная дата)")
    public void testGetSellersWithTransactionsLessThanInvalidDate() throws Exception {
        BigDecimal amount = new BigDecimal("1801");
        LocalDate end = LocalDate.of(2024, 1, 1);
        LocalDate start = LocalDate.of(2024, 2, 28);
        boolean active = true;

        mockMvc.perform(get("/api/analytics/less-then")
                        .param("amount", amount.toString())
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .param("active", Boolean.toString(active))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Введенная начальная дата не должна быть позже конечной даты"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на получение лучших периодов транзакций для продавца")
    public void testGetBestTransactionPeriod() throws Exception {
        Seller seller1 = sellerRepository.findAll().get(0);

        mockMvc.perform(get("/api/analytics/best-periods/seller/{id}", seller1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bestDayPeriod")
                        .isNotEmpty())
                .andExpect(jsonPath("$.bestDayPeriod.transactionCount")
                        .value(2))
                .andExpect(jsonPath("$.bestDayPeriod.startDate")
                        .value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$.bestDayPeriod.endDate")
                        .value("2024-01-01T23:59:59"))
                .andExpect(jsonPath("$.bestDayPeriod.periodType")
                        .value("DAY"))
                .andExpect(jsonPath("$.bestWeekPeriod")
                        .isNotEmpty())
                .andExpect(jsonPath("$.bestWeekPeriod.transactionCount")
                        .value(2))
                .andExpect(jsonPath("$.bestWeekPeriod.startDate")
                        .value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$.bestWeekPeriod.endDate")
                        .value("2024-01-07T23:59:59"))
                .andExpect(jsonPath("$.bestWeekPeriod.periodType")
                        .value("WEEK"))
                .andExpect(jsonPath("$.bestMonthPeriod")
                        .isNotEmpty())
                .andExpect(jsonPath("$.bestMonthPeriod.transactionCount")
                        .value(2))
                .andExpect(jsonPath("$.bestMonthPeriod.startDate")
                        .value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$.bestMonthPeriod.endDate")
                        .value("2024-01-31T23:59:59"))
                .andExpect(jsonPath("$.bestMonthPeriod.periodType")
                        .value("MONTH"))
                .andDo(print());
    }
}
