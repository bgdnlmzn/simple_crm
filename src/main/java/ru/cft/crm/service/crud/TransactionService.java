package ru.cft.crm.service.crud;

import ru.cft.crm.dto.transaction.TransactionCreateRequest;
import ru.cft.crm.dto.transaction.TransactionResponse;
import ru.cft.crm.dto.transaction.TransactionUpdateRequest;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionCreateRequest body);

    TransactionResponse getTransaction(Long transactionId);

    List<TransactionResponse> getAllSellersTransactions(Long sellerId);

    List<TransactionResponse> getAllActiveTransactions();

    List<TransactionResponse> getAllTransactions();

    void deleteTransaction(Long transactionId);

    TransactionResponse updateTransaction(Long transactionId, TransactionUpdateRequest body);
}
