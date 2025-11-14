package ru.cft.crm.service.crud.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cft.crm.model.transaction.TransactionCreateRequest;
import ru.cft.crm.model.transaction.TransactionResponse;
import ru.cft.crm.model.transaction.TransactionUpdateRequest;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.exception.EntityUpdateException;
import ru.cft.crm.exception.InvalidPaymentTypeException;
import ru.cft.crm.exception.SellerNotFoundException;
import ru.cft.crm.exception.TransactionNotFoundException;
import ru.cft.crm.history.HistorySaver;
import ru.cft.crm.mapper.TransactionMapper;
import ru.cft.crm.repository.SellerRepository;
import ru.cft.crm.repository.TransactionRepository;
import ru.cft.crm.service.crud.TransactionService;
import ru.cft.crm.type.ChangeType;
import ru.cft.crm.type.PaymentType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final SellerRepository sellerRepository;

    private final HistorySaver historySaver;

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionCreateRequest body) {
        Seller seller = findSellerById(body.sellerId());
        PaymentType paymentType = validatePaymentType(body.paymentType());

        Transaction transaction = TransactionMapper
                .mapRequestToTransaction(
                        body,
                        seller,
                        paymentType
                );
        transactionRepository.save(transaction);

        return TransactionMapper.mapTransactionToResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(Long transactionId) {
        return TransactionMapper.
                mapTransactionToResponse(findTransactionById(transactionId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllSellersTransactions(Long sellerId) {
        Seller seller = findSellerById(sellerId);

        List<Transaction> transactions = transactionRepository
                .findBySellerAndIsActiveTrue(seller);

        checkIfTransactionsIsEmpty(transactions);
        return TransactionMapper.mapTransactionsToResponses(transactions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllActiveTransactions() {
        List<Transaction> transactions = transactionRepository
                .findByIsActiveTrue();

        checkIfTransactionsIsEmpty(transactions);
        return TransactionMapper.mapTransactionsToResponses(transactions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        checkIfTransactionsIsEmpty(transactions);
        return TransactionMapper.mapTransactionsToResponses(transactions);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long transactionId) {
        Transaction transaction = findTransactionById(transactionId);
        historySaver.saveTransactionHistory(transaction, ChangeType.DELETED);

        transaction.setIsActive(false);
        transaction.setUpdatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(
            Long transactionId,
            TransactionUpdateRequest body
    ) {
        if (body.amount() == null && body.paymentType() == null) {
            throw new EntityUpdateException(
                    "Поля для изменения не должны быть пустыми");
        }
        Transaction transaction = findTransactionById(transactionId);
        historySaver.saveTransactionHistory(transaction, ChangeType.UPDATED);

        updateTransactionFields(transaction, body);
        transactionRepository.save(transaction);

        return TransactionMapper.mapTransactionToResponse(transaction);
    }

    private void updateTransactionFields(
            Transaction transaction,
            TransactionUpdateRequest body
    ) {
        if (body.amount() != null) {
            transaction.setAmount(body.amount());
        }

        if (body.paymentType() != null) {
            PaymentType paymentType = validatePaymentType(body.paymentType());
            transaction.setPaymentType(paymentType);
        }

        transaction.setUpdatedAt(LocalDateTime.now());
    }

    private Transaction findTransactionById(Long transactionId) {
        return transactionRepository.findByIdAndIsActive(
                        transactionId, true)
                .orElseThrow(() -> new TransactionNotFoundException("Транзакция с id: "
                                + transactionId + " не найдена"
                        )
                );
    }

    private Seller findSellerById(Long sellerId) {
        return sellerRepository.findByIdAndIsActiveTrue(sellerId)
                .orElseThrow(() -> new SellerNotFoundException("Продавец с id: "
                                + sellerId + " не найден"
                        )
                );
    }

    private PaymentType validatePaymentType(String paymentTypeStr) {
        if (paymentTypeStr == null || paymentTypeStr.isEmpty()) {
            throw new InvalidPaymentTypeException("Тип оплаты не должен быть пустым");
        }

        try {
            return PaymentType.valueOf(paymentTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentTypeException("Неверный тип оплаты: "
                    + paymentTypeStr
                    + ". Доступные типы оплаты: "
                    + Arrays.toString(PaymentType.values()));
        }
    }

    private void checkIfTransactionsIsEmpty(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException("Ни одной транзакции не найдено");
        }
    }
}
