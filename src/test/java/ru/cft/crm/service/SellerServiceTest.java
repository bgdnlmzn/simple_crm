package ru.cft.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.cft.crm.dto.seller.SellerCreateRequest;
import ru.cft.crm.dto.seller.SellerResponse;
import ru.cft.crm.dto.seller.SellerUpdateRequest;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.exception.EntityUpdateException;
import ru.cft.crm.exception.SellerAlreadyExistsException;
import ru.cft.crm.exception.SellerNotFoundException;
import ru.cft.crm.history.HistorySaver;
import ru.cft.crm.repository.SellerRepository;
import ru.cft.crm.repository.TransactionRepository;
import ru.cft.crm.service.crud.SellerService;
import ru.cft.crm.type.ChangeType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Тесты SellerService")
public class SellerServiceTest {
    @MockBean
    private SellerRepository sellerRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private HistorySaver historySaver;

    @Autowired
    private SellerService sellerService;

    private Seller seller;
    private SellerCreateRequest createRequest;
    private SellerUpdateRequest updateRequest;
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        seller = new Seller();
        seller.setId(1L);
        seller.setSellerName("Test Seller");
        seller.setContactInfo("seller@test.com");
        seller.setIsActive(true);

        createRequest = new SellerCreateRequest("Test Seller", "seller@test.com");
        updateRequest = new SellerUpdateRequest("Updated Seller", "updated@test.com");

        transaction = new Transaction();
        transaction.setSeller(seller);
        transaction.setIsActive(true);
    }

    @Test
    @DisplayName("Тест на создание продавца")
    public void testCreateSeller() {
        when(sellerRepository.existsByContactInfo(anyString())).thenReturn(false);

        SellerResponse response = sellerService.createSeller(createRequest);

        assertThat(response.sellerName()).isEqualTo("Test Seller");
        verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    @DisplayName("Тест на создание продавца с уже существующим email")
    public void testCreateSellerAlreadyExists() {
        when(sellerRepository.existsByContactInfo(anyString())).thenReturn(true);

        assertThatThrownBy(() -> sellerService.createSeller(createRequest))
                .isInstanceOf(SellerAlreadyExistsException.class)
                .hasMessage("Продавец с таким email уже существует");

        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    @DisplayName("Тест на получение продавца")
    public void testGetSeller() {
        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(seller));

        SellerResponse response = sellerService.getSeller(1L);

        assertThat(response.sellerName()).isEqualTo("Test Seller");
        verify(sellerRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    @DisplayName("Тест на выброс ошибки, когда продавец не найден")
    public void testGetSellerNotFound() {
        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sellerService.getSeller(1L))
                .isInstanceOf(SellerNotFoundException.class)
                .hasMessageContaining("Продавец с id: 1 не найден");

        verify(sellerRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    @DisplayName("Тест на получение всех продавцов(активных)")
    public void testGetAllActiveSellers() {
        when(sellerRepository.findByIsActiveTrue()).thenReturn(List.of(seller));

        List<SellerResponse> responses = sellerService.getAllActiveSellers();

        assertThat(responses).hasSize(1);
        verify(sellerRepository).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Тест на получение всех продавцов")
    public void testGetAllSellers() {
        when(sellerRepository.findAll()).thenReturn(List.of(seller));

        List<SellerResponse> responses = sellerService.getAllSellers();

        assertThat(responses).hasSize(1);
        verify(sellerRepository).findAll();
    }

    @Test
    @DisplayName("Тест на получение всех пользователей, когда не нашло ни одного")
    public void testGetAllSellersEmpty() {
        when(sellerRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> sellerService.getAllSellers())
                .isInstanceOf(SellerNotFoundException.class)
                .hasMessage("Ни одного пользователя не найдено");

        verify(sellerRepository).findAll();
    }

    @Test
    @DisplayName("Тест на выброс ошибки, когда не нашло ни одного пользователя")
    public void testGetAllActiveSellersEmpty() {
        when(sellerRepository.findByIsActiveTrue()).thenReturn(List.of());

        assertThatThrownBy(() -> sellerService.getAllActiveSellers())
                .isInstanceOf(SellerNotFoundException.class)
                .hasMessage("Ни одного пользователя не найдено");

        verify(sellerRepository).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Тест на обновление продавца")
    public void testUpdateSeller() {
        when(sellerRepository.existsByContactInfo(anyString())).thenReturn(false);
        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(seller));

        SellerResponse response = sellerService.updateSeller(1L, updateRequest);

        assertThat(response.sellerName()).isEqualTo("Updated Seller");
        verify(sellerRepository).save(any(Seller.class));
        verify(historySaver).saveSellerHistory(any(Seller.class), eq(ChangeType.UPDATED));
    }

    @Test
    @DisplayName("Тест на удаление продавца")
    public void testDeleteSeller() {
        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.findBySellerAndIsActiveTrue(seller)).thenReturn(List.of(transaction));

        sellerService.deleteSeller(1L);

        verify(sellerRepository).save(any(Seller.class));
        verify(historySaver).saveSellerHistory(any(Seller.class), eq(ChangeType.DELETED));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Тест на выброс ошибки, когда поля на обновление пустые")
    public void testUpdateSellerWithNoFieldsToUpdate() {
        SellerUpdateRequest emptyUpdateRequest = new SellerUpdateRequest(null, null);

        assertThatThrownBy(() -> sellerService.updateSeller(1L, emptyUpdateRequest))
                .isInstanceOf(EntityUpdateException.class)
                .hasMessage("Поля для изменения не должны быть пустыми");

        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    @DisplayName("Тест на удаление транзакций продавца, при его удалении")
    public void testDeleteSellerWithTransactionsDeactivation() {
        when(sellerRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.findBySellerAndIsActiveTrue(seller)).thenReturn(List.of(transaction));

        sellerService.deleteSeller(1L);

        assertThat(transaction.getIsActive()).isFalse();
        verify(transactionRepository).save(any(Transaction.class));
    }
}
