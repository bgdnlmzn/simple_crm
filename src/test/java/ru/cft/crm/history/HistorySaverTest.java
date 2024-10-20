package ru.cft.crm.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.SellersHistory;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.entity.TransactionsHistory;
import ru.cft.crm.repository.SellersHistoryRepository;
import ru.cft.crm.repository.TransactionsHistoryRepository;
import ru.cft.crm.type.ChangeType;
import ru.cft.crm.type.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class HistorySaverTest {
    @MockBean
    private SellersHistoryRepository sellersHistoryRepository;

    @MockBean
    private TransactionsHistoryRepository transactionsHistoryRepository;

    @Autowired
    private HistorySaver historySaver;

    private Seller seller;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        seller = new Seller();
        seller.setId(1L);
        seller.setSellerName("Test Seller");
        seller.setContactInfo("testSeller@mail.ru");
        seller.setRegistrationDate(LocalDateTime.of(2024,
                1,
                1,
                0,
                0)
        );
        seller.setUpdatedAt(LocalDateTime.now());

        transaction = new Transaction();
        transaction.setId(10L);
        transaction.setSeller(seller);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setPaymentType(PaymentType.CARD);
        transaction.setTransactionDate(LocalDateTime.of(2024,
                1,
                1,
                0,
                0)
        );
        transaction.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Тест на сохранение истории продавца")
    public void testSaveSellerHistory() {
        ChangeType changeType = ChangeType.UPDATED;

        historySaver.saveSellerHistory(seller, changeType);

        ArgumentCaptor<SellersHistory> captor = ArgumentCaptor.forClass(SellersHistory.class);
        verify(sellersHistoryRepository).save(captor.capture());

        SellersHistory savedHistory = captor.getValue();

        assertThat(savedHistory.getSellerId()).isEqualTo(seller.getId());
        assertThat(savedHistory.getSellerName()).isEqualTo(seller.getSellerName());
        assertThat(savedHistory.getContactInfo()).isEqualTo(seller.getContactInfo());
        assertThat(savedHistory.getRegistrationDate()).isEqualTo(seller.getRegistrationDate());
        assertThat(savedHistory.getUpdatedAt()).isEqualTo(seller.getUpdatedAt());
        assertThat(savedHistory.getChangeType()).isEqualTo(changeType);
        assertThat(savedHistory.getChangeTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Тест на сохранение истории транзакции")
    public void testSaveTransactionHistory() {
        ChangeType changeType = ChangeType.DELETED;

        historySaver.saveTransactionHistory(transaction, changeType);

        ArgumentCaptor<TransactionsHistory> captor = ArgumentCaptor.forClass(TransactionsHistory.class);
        verify(transactionsHistoryRepository).save(captor.capture());

        TransactionsHistory savedHistory = captor.getValue();

        assertThat(savedHistory.getTransactionId()).isEqualTo(transaction.getId());
        assertThat(savedHistory.getSellerId()).isEqualTo(transaction.getSeller().getId());
        assertThat(savedHistory.getAmount()).isEqualTo(transaction.getAmount());
        assertThat(savedHistory.getPaymentType()).isEqualTo(transaction.getPaymentType());
        assertThat(savedHistory.getTransactionDate()).isEqualTo(transaction.getTransactionDate());
        assertThat(savedHistory.getUpdatedAt()).isEqualTo(transaction.getUpdatedAt());
        assertThat(savedHistory.getChangeType()).isEqualTo(changeType);
        assertThat(savedHistory.getChangeTimestamp()).isNotNull();
    }
}
