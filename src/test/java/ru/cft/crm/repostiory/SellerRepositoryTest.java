package ru.cft.crm.repostiory;

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
import ru.cft.crm.repository.SellerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@DisplayName("Тесты для SellerRepository")
public class SellerRepositoryTest {
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
    private SellerRepository sellerRepository;


    @Test
    @DisplayName("Тест на поиск всех активных продавцов")
    public void testFindByIsActiveTrue() {
        Seller seller = new Seller();
        seller.setSellerName("Test Seller");
        seller.setIsActive(true);
        seller.setContactInfo("seller@test.com");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());
        sellerRepository.save(seller);

        List<Seller> activeSellers = sellerRepository.findByIsActiveTrue();

        assertThat(activeSellers).isNotEmpty();
        assertThat(activeSellers.get(0).getSellerName()).isEqualTo("Test Seller");
    }

    @Test
    @DisplayName("Тест на поиск активных продавцов по id")
    public void testFindByIdAndIsActiveTrue() {
        Seller seller = new Seller();
        seller.setSellerName("Test Seller");
        seller.setIsActive(true);
        seller.setContactInfo("seller@test.com");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());
        sellerRepository.save(seller);

        Optional<Seller> activeSeller = sellerRepository.findByIdAndIsActiveTrue(seller.getId());

        assertThat(activeSeller).isPresent();
        assertThat(activeSeller.get().getSellerName()).isEqualTo("Test Seller");
    }

    @Test
    @DisplayName("Тест проверки продавца с заданными контактными данными")
    void testExistsByContactInfo() {
        Seller seller = new Seller();
        seller.setSellerName("Test Seller");
        seller.setIsActive(true);
        seller.setContactInfo("seller@test.com");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());
        sellerRepository.save(seller);

        Boolean exists = sellerRepository.existsByContactInfo("seller@test.com");

        assertThat(exists).isTrue();
    }
}
