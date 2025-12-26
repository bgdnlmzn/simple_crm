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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DisplayName("Тесты TransactionController")
public class TransactionControllerTest {
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
    private TransactionRepository transactionRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @BeforeEach
    public void setUp() {
        transactionRepository.deleteAll();
        sellerRepository.deleteAll();

        Seller seller = new Seller();
        seller.setSellerName("Test Seller");
        seller.setContactInfo("testSeller@mail.ru");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());
        seller.setIsActive(true);
        sellerRepository.save(seller);

        Transaction transaction = new Transaction();
        transaction.setSeller(seller);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setPaymentType(PaymentType.CARD);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setIsActive(true);
        transactionRepository.save(transaction);

        Transaction transaction2 = new Transaction();
        transaction2.setSeller(seller);
        transaction2.setAmount(new BigDecimal("200.00"));
        transaction2.setPaymentType(PaymentType.CARD);
        transaction2.setTransactionDate(LocalDateTime.now());
        transaction2.setUpdatedAt(LocalDateTime.now());
        transaction2.setIsActive(true);
        transactionRepository.save(transaction2);
    }

    @Test
    @DisplayName("Тест на создание транзакции")
    public void testCreateTransaction() throws Exception {
        Seller seller = sellerRepository.findAll().get(0);
        String requestBody = """
                {
                  "amount": 100.00,
                  "paymentType": "CARD",
                  "sellerId": %d
                }
                """.formatted(seller.getId());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.paymentType").value("CARD"))
                .andExpect(jsonPath("$.sellerId").value(seller.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на создание транзакции несуществующего продавца")
    public void testCreateTransactionWithSellerNotFound() throws Exception {
        String requestBody = """
                {
                  "amount": 100.00,
                  "paymentType": "CARD",
                  "sellerId": 52
                }
                """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Продавец с id: 52 не найден"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на создание транзакции с некорректной суммой")
    public void testCreateTransactionWithInvalidAmount() throws Exception {
        Seller seller = sellerRepository.findAll().get(0);

        String requestBody = """
                {
                  "amount": -100,
                  "paymentType": "CARD",
                  "sellerId": %d
                }
                """.formatted(seller.getId());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Ошибка валидации"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на создание транзакции с пустыми полями")
    public void testCreateTransactionWithEmptyFields() throws Exception {
        String requestBody = """
                {
                  "amount": "",
                  "paymentType": "",
                  "sellerId":""
                }
                """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Ошибка валидации"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на создание транзакции c некорректным типом оплаты")
    public void testCreateTransactionWithInvalidPaymentType() throws Exception {
        Seller seller = sellerRepository.findAll().get(0);

        String requestBody = """
                {
                  "amount": 100,
                  "paymentType": "HAMSTER_COIN",
                  "sellerId": %d
                }
                """.formatted(seller.getId());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Неверный тип оплаты: HAMSTER_COIN"
                                + ". Доступные типы оплаты: "
                                + Arrays.toString(PaymentType.values())))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на получение транзакции по ID")
    public void testGetTransactionById() throws Exception {
        Seller seller = sellerRepository.findAll().get(0);
        Transaction transaction = transactionRepository.findAll().get(0);

        mockMvc.perform(get("/api/transactions/" + transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transaction.getId()))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.paymentType").value("CARD"))
                .andExpect(jsonPath("$.sellerId").value(seller.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на получение всех транзакций продавца")
    public void testGetAllSellersTransactions() throws Exception {
        Seller seller = sellerRepository.findAll().get(0);

        mockMvc.perform(get("/api/transactions/seller/" + seller.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[1].amount").value(200.00))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на удаление транзакции")
    public void testDeleteTransactionById() throws Exception {
        Transaction transaction = transactionRepository.findAll().get(0);

        mockMvc.perform(delete("/api/transactions/" + transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на обновление транзакции")
    public void testUpdateTransactionById() throws Exception {
        Seller seller = sellerRepository.findAll().get(0);
        Transaction transaction = transactionRepository.findAll().get(0);

        String requestBody = """
                {
                  "amount": 1000,
                  "paymentType": "CASH"
                }
                """;

        mockMvc.perform(patch("/api/transactions/" + transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transaction.getId()))
                .andExpect(jsonPath("$.amount").value(1000.00))
                .andExpect(jsonPath("$.paymentType").value("CASH"))
                .andExpect(jsonPath("$.sellerId").value(seller.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на обновление транзакции с пустыми полями")
    public void testUpdateTransactionWithEmptyFields() throws Exception {
        Transaction transaction = transactionRepository.findAll().get(0);

        String requestBody = """
                {
                  "amount":"",
                  "paymentType":""
                }
                """;

        mockMvc.perform(patch("/api/transactions/" + transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Тип оплаты не должен быть пустым"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на обновление транзакции с некорректной суммой")
    public void testUpdateTransactionWithInvalidAmount() throws Exception {
        Transaction transaction = transactionRepository.findAll().get(0);

        String requestBody = """
                {
                  "amount":-100,
                  "paymentType":"CARD"
                }
                """;

        mockMvc.perform(patch("/api/transactions/" + transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Ошибка валидации"))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на обновление транзакции c некорректным типом оплаты")
    public void testUpdateTransactionWithInvalidPaymentType() throws Exception {
        Transaction transaction = transactionRepository.findAll().get(0);

        String requestBody = """
                {
                  "amount":100,
                  "paymentType":"HAMSTER_COIN"
                }
                """;

        mockMvc.perform(patch("/api/transactions/" + transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Неверный тип оплаты: HAMSTER_COIN"
                                + ". Доступные типы оплаты: "
                                + Arrays.toString(PaymentType.values())))
                .andDo(print());
    }
}
