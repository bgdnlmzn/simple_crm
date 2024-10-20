package ru.cft.crm.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.cft.crm.dto.analitycs.MostProductiveSellerResponse;
import ru.cft.crm.dto.analitycs.SellerWithTransactionsResponse;
import ru.cft.crm.dto.seller.SellerCreateRequest;
import ru.cft.crm.dto.seller.SellerResponse;
import ru.cft.crm.entity.Seller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты для SellerMapper")
class SellerMapperTest {
    @Test
    @DisplayName("Тест на корректный мап запроса в сущность Seller")
    void testMapDtoToSeller() {
        SellerCreateRequest request = new SellerCreateRequest(
                "Test Seller",
                "testSeller@mail.ru");

        Seller seller = SellerMapper.mapDtoToSeller(request);

        assertThat(seller).isNotNull();
        assertThat(seller.getSellerName()).isEqualTo("Test Seller");
        assertThat(seller.getContactInfo()).isEqualTo("testSeller@mail.ru");
        assertThat(seller.getIsActive()).isTrue();
        assertThat(seller.getRegistrationDate()).isNotNull();
        assertThat(seller.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Тест на корректный мап сущности Seller")
    void testMapSellerToDto() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setSellerName("Test Seller");
        seller.setContactInfo("testSeller@mail.ru");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());
        seller.setIsActive(true);

        SellerResponse response = SellerMapper.mapSellerToDto(seller);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.sellerName()).isEqualTo("Test Seller");
        assertThat(response.contactInfo()).isEqualTo("testSeller@mail.ru");
        assertThat(response.isActive()).isTrue();
        assertThat(response.registrationDate()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Тест на корректный мап продавца с его транзакциями")
    void testMapSellerWithTransactions() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setSellerName("Test Seller");
        seller.setContactInfo("testSeller@mail.ru");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());
        seller.setIsActive(true);

        BigDecimal totalAmount = new BigDecimal("1000.00");

        SellerWithTransactionsResponse response = SellerMapper.mapSellerWithTransactions(seller, totalAmount);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.sellerName()).isEqualTo("Test Seller");
        assertThat(response.transactionAmount()).isEqualTo(totalAmount);
        assertThat(response.isActive()).isTrue();
    }

    @Test
    @DisplayName("Тест на корректный мап списка продавцов")
    void testMapSellersToResponses() {
        Seller seller1 = new Seller();
        seller1.setId(1L);
        seller1.setSellerName("Seller 1");
        seller1.setContactInfo("seller1@mail.ru");

        Seller seller2 = new Seller();
        seller2.setId(2L);
        seller2.setSellerName("Seller 2");
        seller2.setContactInfo("seller2@mail.ru");

        List<Seller> sellers = List.of(seller1, seller2);

        List<SellerResponse> responses = SellerMapper.mapSellersToResponses(sellers);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(1).id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Тест на корректный мап самого продуктивного продавца")
    void testMapToMostProductiveSellerResponse() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setSellerName("Test Seller");
        seller.setContactInfo("testSeller@mail.ru");
        seller.setRegistrationDate(LocalDateTime.now());

        BigDecimal totalAmount = new BigDecimal("5000.00");

        MostProductiveSellerResponse response = SellerMapper.mapToMostProductiveSellerResponse(seller, totalAmount);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.sellerName()).isEqualTo("Test Seller");
        assertThat(response.amount()).isEqualTo(totalAmount);
    }
}
