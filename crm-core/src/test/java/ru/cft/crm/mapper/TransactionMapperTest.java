package ru.cft.crm.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.cft.crm.model.transaction.TransactionCreateRequest;
import ru.cft.crm.model.transaction.TransactionResponse;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.type.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты TransactionMapper")
class TransactionMapperTest {
    @Test
    @DisplayName("Тест на корректный мап запроса в сущность Transaction")
    void testMapRequestToTransaction() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setSellerName("Test Seller");

        TransactionCreateRequest request = new TransactionCreateRequest(
                1L,
                new BigDecimal("100.00"),
                "CARD");

        PaymentType paymentType = PaymentType.CARD;

        Transaction transaction = TransactionMapper.mapRequestToTransaction(request, seller, paymentType);

        assertThat(transaction).isNotNull();
        assertThat(transaction.getSeller()).isEqualTo(seller);
        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(transaction.getPaymentType()).isEqualTo(paymentType);
        assertThat(transaction.getTransactionDate()).isNotNull();
        assertThat(transaction.getUpdatedAt()).isNotNull();
        assertThat(transaction.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Тест на корректный мап транзакции")
    void testMapTransactionToResponse() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setSellerName("Test Seller");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setSeller(seller);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setPaymentType(PaymentType.CARD);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setIsActive(true);

        TransactionResponse response = TransactionMapper.mapTransactionToResponse(transaction);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.sellerId()).isEqualTo(1L);
        assertThat(response.amount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(response.paymentType()).isEqualTo(PaymentType.CARD);
        assertThat(response.transactionDate()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
        assertThat(response.isActive()).isTrue();
    }

    @Test
    @DisplayName("Тест на корректный мап списка транзакций")
    void testMapTransactionsToResponses() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setSellerName("Test Seller");

        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setSeller(seller);
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setPaymentType(PaymentType.CARD);
        transaction1.setTransactionDate(LocalDateTime.now());
        transaction1.setUpdatedAt(LocalDateTime.now());
        transaction1.setIsActive(true);

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setSeller(seller);
        transaction2.setAmount(new BigDecimal("200.00"));
        transaction2.setPaymentType(PaymentType.CASH);
        transaction2.setTransactionDate(LocalDateTime.now());
        transaction2.setUpdatedAt(LocalDateTime.now());
        transaction2.setIsActive(false);

        List<Transaction> transactions = List.of(transaction1, transaction2);

        List<TransactionResponse> responses = TransactionMapper.mapTransactionsToResponses(transactions);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).sellerId()).isEqualTo(1L);
        assertThat(responses.get(0).amount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(responses.get(0).paymentType()).isEqualTo(PaymentType.CARD);

        assertThat(responses.get(1).id()).isEqualTo(2L);
        assertThat(responses.get(1).sellerId()).isEqualTo(1L);
        assertThat(responses.get(1).amount()).isEqualTo(new BigDecimal("200.00"));
        assertThat(responses.get(1).paymentType()).isEqualTo(PaymentType.CASH);
    }
}
