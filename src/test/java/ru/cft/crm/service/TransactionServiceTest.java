package ru.cft.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.cft.crm.dto.transaction.TransactionCreateRequest;
import ru.cft.crm.dto.transaction.TransactionResponse;
import ru.cft.crm.dto.transaction.TransactionUpdateRequest;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.exception.EntityUpdateException;
import ru.cft.crm.exception.InvalidPaymentTypeException;
import ru.cft.crm.exception.SellerNotFoundException;
import ru.cft.crm.exception.TransactionNotFoundException;
import ru.cft.crm.history.HistorySaver;
import ru.cft.crm.repository.SellerRepository;
import ru.cft.crm.repository.TransactionRepository;
import ru.cft.crm.service.crud.TransactionService;
import ru.cft.crm.type.ChangeType;
import ru.cft.crm.type.PaymentType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Тесты TransactionService")
public class TransactionServiceTest {
    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private SellerRepository sellerRepository;

    @MockBean
    private HistorySaver historySaver;

    @Autowired
    private TransactionService transactionService;

    private Seller seller;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        seller = new Seller();
        seller.setId(1L);
        seller.setSellerName("Test Seller");
        seller.setIsActive(true);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setSeller(seller);
        transaction.setAmount(new BigDecimal("100"));
        transaction.setPaymentType(PaymentType.CARD);
        transaction.setIsActive(true);
    }

    @Test
    @DisplayName("Тест на создание транзакции")
    public void testCreateTransaction() {
        TransactionCreateRequest request = new TransactionCreateRequest(
                1L, new BigDecimal("100"), "CARD"
        );

        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(seller));

        TransactionResponse response = transactionService.createTransaction(request);

        assertThat(response.amount()).isEqualTo(new BigDecimal("100"));
        assertThat(response.paymentType().toString()).isEqualTo("CARD");

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Тест на создание транзакции с пустым типом оплаты")
    public void testCreateWithPaymentTypeEmpty() {
        TransactionCreateRequest request = new TransactionCreateRequest(
                1L, new BigDecimal("100"), ""
        );

        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(seller));

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(InvalidPaymentTypeException.class)
                .hasMessage("Тип оплаты не должен быть пустым");


        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Тест на создание транзакции с null типом оплаты")
    public void testCreateWithPaymentTypeNull() {
        TransactionCreateRequest request = new TransactionCreateRequest(
                1L, new BigDecimal("100"), null
        );

        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(seller));

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(InvalidPaymentTypeException.class)
                .hasMessage("Тип оплаты не должен быть пустым");


        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Тест на создание транзакции с некорректным типом оплаты")
    public void testCreateWithPaymentTypeInvalid() {
        TransactionCreateRequest request = new TransactionCreateRequest(
                1L, new BigDecimal("100"), "CASHBACK"
        );

        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(seller));

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(InvalidPaymentTypeException.class)
                .hasMessage("Неверный тип оплаты: CASHBACK. Доступные типы оплаты: [CASH, CARD, TRANSFER]");


        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Тест на создание транзакции с несуществующим продавцом")
    public void testCreateTransactionSellerNotFound() {
        TransactionCreateRequest request = new TransactionCreateRequest(
                1L, new BigDecimal("100"), "CARD"
        );

        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(SellerNotFoundException.class)
                .hasMessage("Продавец с id: 1 не найден");

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Тест на получение транзакции")
    public void testGetTransaction() {
        when(transactionRepository.findByIdAndIsActive(1L, true))
                .thenReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.getTransaction(1L);

        assertThat(response.amount()).isEqualTo(new BigDecimal("100"));
        verify(transactionRepository).findByIdAndIsActive(1L, true);
    }

    @Test
    @DisplayName("Тест на получение транзакции, если она не найдена")
    public void testGetTransactionNotFound() {
        when(transactionRepository.findByIdAndIsActive(1L, true))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransaction(1L))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Транзакция с id: 1 не найдена");

        verify(transactionRepository).findByIdAndIsActive(1L, true);
    }

    @Test
    @DisplayName("Тест на получение всех активных транзакций продавца")
    public void testGetAllSellersTransactions() {
        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.findBySellerAndIsActiveTrue(seller))
                .thenReturn(List.of(transaction));

        List<TransactionResponse> responses = transactionService.getAllSellersTransactions(1L);

        assertThat(responses).hasSize(1);
        verify(transactionRepository).findBySellerAndIsActiveTrue(seller);
    }

    @Test
    @DisplayName("Тест на получение всех активных транзакций продавца, если транзакции не найдены")
    public void testGetAllSellersTransactionsEmpty() {
        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.findBySellerAndIsActiveTrue(seller))
                .thenReturn(List.of());

        assertThatThrownBy(() -> transactionService.getAllSellersTransactions(1L))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Ни одной транзакции не найдено");

        verify(transactionRepository).findBySellerAndIsActiveTrue(seller);
    }

    @Test
    @DisplayName("Тест на обновление транзакции")
    public void testUpdateTransaction() {
        TransactionUpdateRequest request = new TransactionUpdateRequest(
                new BigDecimal("200"), "CASH"
        );

        when(transactionRepository.findByIdAndIsActive(1L, true)).thenReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.updateTransaction(1L, request);

        assertThat(response.amount()).isEqualTo(new BigDecimal("200"));
        assertThat(response.paymentType().toString()).isEqualTo("CASH");
        verify(transactionRepository).save(any(Transaction.class));
        verify(historySaver).saveTransactionHistory(any(Transaction.class), eq(ChangeType.UPDATED));
    }

    @Test
    @DisplayName("Тест на обновление транзакции без изменяемых полей")
    public void testUpdateTransactionWithNoFieldsToUpdate() {
        TransactionUpdateRequest emptyUpdateRequest = new TransactionUpdateRequest(null, null);

        assertThatThrownBy(() -> transactionService.updateTransaction(1L, emptyUpdateRequest))
                .isInstanceOf(EntityUpdateException.class)
                .hasMessage("Поля для изменения не должны быть пустыми");

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Тест на удаление транзакции")
    public void testDeleteTransaction() {
        when(transactionRepository.findByIdAndIsActive(1L, true)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(1L);

        assertThat(transaction.getIsActive()).isFalse();
        verify(transactionRepository).save(any(Transaction.class));
        verify(historySaver).saveTransactionHistory(any(Transaction.class), eq(ChangeType.DELETED));
    }

    @Test
    @DisplayName("Тест на проверку получения всех активных транзакций")
    public void testGetAllActiveTransactions() {
        when(transactionRepository.findByIsActiveTrue()).thenReturn(List.of(transaction));

        List<TransactionResponse> responses = transactionService.getAllActiveTransactions();

        assertThat(responses).hasSize(1);
        verify(transactionRepository).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Тест на проверку получения всех активных транзакций, когда они отсутствуют")
    public void testGetAllActiveTransactionsIsEmpty() {
        when(transactionRepository.findByIsActiveTrue()).thenReturn(List.of());

        assertThatThrownBy(() -> transactionService.getAllActiveTransactions())
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Ни одной транзакции не найдено");

        verify(transactionRepository).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Тест на получение всех транзакций (активных и неактивных)")
    public void testGetAllTransactions() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        List<TransactionResponse> responses = transactionService.getAllTransactions();

        assertThat(responses).hasSize(1);
        verify(transactionRepository).findAll();
    }

    @Test
    @DisplayName("Тест на проверку получения всех транзакций, когда они отсутствуют")
    public void testGetAllTransactionsIsEmpty() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> transactionService.getAllTransactions())
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Ни одной транзакции не найдено");

        verify(transactionRepository).findAll();
    }
}
