package ru.cft.crm.service.crud.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cft.crm.model.seller.SellerCreateRequest;
import ru.cft.crm.model.seller.SellerResponse;
import ru.cft.crm.model.seller.SellerUpdateRequest;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.exception.EntityUpdateException;
import ru.cft.crm.exception.SellerAlreadyExistsException;
import ru.cft.crm.exception.SellerNotFoundException;
import ru.cft.crm.history.HistorySaver;
import ru.cft.crm.mapper.SellerMapper;
import ru.cft.crm.repository.SellerRepository;
import ru.cft.crm.repository.TransactionRepository;
import ru.cft.crm.service.crud.SellerService;
import ru.cft.crm.type.ChangeType;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;

    private final TransactionRepository transactionRepository;

    private final HistorySaver historySaver;

    @Override
    @Transactional
    public SellerResponse createSeller(SellerCreateRequest body) {
        checkAlreadyCreated(body.contactInfo());

        Seller seller = SellerMapper.mapDtoToSeller(body);
        sellerRepository.save(seller);

        return SellerMapper.mapSellerToDto(seller);
    }

    @Override
    @Transactional(readOnly = true)
    public SellerResponse getSeller(Long sellerId) {
        return SellerMapper.mapSellerToDto(findSellerById(sellerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SellerResponse> getAllActiveSellers() {
        List<Seller> sellers = sellerRepository.findByIsActiveTrue();

        checkIfSellersIsEmpty(sellers);

        return SellerMapper.mapSellersToResponses(sellers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SellerResponse> getAllSellers() {
        List<Seller> sellers = sellerRepository.findAll();

        checkIfSellersIsEmpty(sellers);

        return SellerMapper.mapSellersToResponses(sellers);
    }

    @Override
    @Transactional
    public SellerResponse updateSeller(Long sellerId, SellerUpdateRequest body) {
        if (body.sellerName() == null && body.contactInfo() == null) {
            throw new EntityUpdateException(
                    "Поля для изменения не должны быть пустыми");
        }

        checkAlreadyCreated(body.contactInfo());

        Seller seller = findSellerById(sellerId);
        historySaver.saveSellerHistory(seller, ChangeType.UPDATED);

        updateSellerFields(seller, body);
        sellerRepository.save(seller);

        return SellerMapper.mapSellerToDto(seller);
    }

    @Override
    @Transactional
    public void deleteSeller(Long sellerId) {
        Seller seller = findSellerById(sellerId);
        historySaver.saveSellerHistory(seller, ChangeType.DELETED);

        seller.setUpdatedAt(LocalDateTime.now());
        seller.setIsActive(false);

        deleteAllSellersTransactions(seller);

        sellerRepository.save(seller);
    }

    private Seller findSellerById(Long sellerId) {
        return sellerRepository.findByIdAndIsActiveTrue(sellerId)
                .orElseThrow(() -> new SellerNotFoundException(
                                "Продавец с id: " + sellerId + " не найден"
                        )
                );
    }

    private void updateSellerFields(Seller seller, SellerUpdateRequest body) {
        if (body.sellerName() != null) {
            seller.setSellerName(body.sellerName());
        }

        if (body.contactInfo() != null) {
            seller.setContactInfo(body.contactInfo());
        }

        seller.setUpdatedAt(LocalDateTime.now());
    }

    private void deleteAllSellersTransactions(Seller seller) {
        List<Transaction> transactions = transactionRepository
                .findBySellerAndIsActiveTrue(seller);

        transactions.forEach(transaction -> {
            historySaver.saveTransactionHistory(transaction, ChangeType.DELETED);

            transaction.setIsActive(false);
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);
        });
    }

    private void checkAlreadyCreated(String contactInfo) {
        if (sellerRepository.existsByContactInfo(contactInfo)) {
            throw new SellerAlreadyExistsException("Продавец с таким email уже существует");
        }
    }

    private void checkIfSellersIsEmpty(List<Seller> sellers) {
        if (sellers.isEmpty()) {
            throw new SellerNotFoundException("Ни одного пользователя не найдено");
        }
    }
}
