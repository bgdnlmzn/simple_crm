package ru.cft.crm.repostiory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@DisplayName("Тесты TransactionRepository")
public class TransactionRepositoryTest {
    @SuppressWarnings("resource")
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("test_pass");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SellerRepository sellerRepository;

    private Seller testSeller;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testSeller = new Seller();
        testSeller.setSellerName("Test Seller");
        testSeller.setIsActive(true);
        testSeller.setContactInfo("seller@test.com");
        testSeller.setRegistrationDate(LocalDateTime.now());
        testSeller.setUpdatedAt(LocalDateTime.now());
        testSeller.setIsActive(true);
        sellerRepository.save(testSeller);

        testTransaction = new Transaction();
        testTransaction.setSeller(testSeller);
        testTransaction.setTransactionDate(LocalDateTime.now());
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setPaymentType(PaymentType.CARD);
        testTransaction.setUpdatedAt(LocalDateTime.now());
        testTransaction.setIsActive(true);
        transactionRepository.save(testTransaction);
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    @Test
    @DisplayName("Поиск активных транзакций")
    void testFindByIsActiveTrue() {
        List<Transaction> activeTransactions = transactionRepository.findByIsActiveTrue();
        assertFalse(activeTransactions.isEmpty());
        assertEquals(testTransaction.getId(), activeTransactions.get(0).getId());
    }

    @Test
    @DisplayName("Поиск транзакций по id продавца")
    void testFindAllBySellerId() {
        List<Transaction> transactions = transactionRepository.findAllBySellerId(testSeller.getId());
        assertFalse(transactions.isEmpty());
        assertEquals(testTransaction.getSeller().getId(), testSeller.getId());
    }

    @Test
    @DisplayName("Поиск активных транзакций по id")
    void testFindByIdAndIsActive() {
        Optional<Transaction> transaction = transactionRepository.findByIdAndIsActive(testTransaction.getId(), true);
        assertTrue(transaction.isPresent());
        assertEquals(testTransaction.getId(), transaction.get().getId());
    }

    @Test
    @DisplayName("Поиск по временному промежутку и активному продавцу")
    void testFindAllByTransactionDateBetweenAndSellerIsActiveTrue() {
        LocalDateTime start = testTransaction.getTransactionDate().minusDays(1);
        LocalDateTime end = testTransaction.getTransactionDate().plusDays(1);
        List<Transaction> transactions = transactionRepository.findAllByTransactionDateBetweenAndSellerIsActiveTrue(start, end);
        assertFalse(transactions.isEmpty());
        assertEquals(testTransaction.getId(), transactions.get(0).getId());
    }
}
