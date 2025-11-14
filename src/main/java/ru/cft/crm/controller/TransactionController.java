package ru.cft.crm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cft.crm.controller.api.TransactionApi;
import ru.cft.crm.model.transaction.TransactionCreateRequest;
import ru.cft.crm.model.transaction.TransactionResponse;
import ru.cft.crm.model.transaction.TransactionUpdateRequest;
import ru.cft.crm.service.crud.TransactionService;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class TransactionController implements TransactionApi {

    private final TransactionService transactionService;

    @Override
    public TransactionResponse createTransaction(TransactionCreateRequest body) {
        return transactionService.createTransaction(body);
    }

    @Override
    public TransactionResponse getTransaction(Long transactionId) {
        return transactionService.getTransaction(transactionId);
    }

    @Override
    public List<TransactionResponse> getAllSellersTransactions(Long sellerId) {
        return transactionService.getAllSellersTransactions(sellerId);
    }

    @Override
    public List<TransactionResponse> getAllActiveTransactions() {
        return transactionService.getAllActiveTransactions();
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @Override
    public TransactionResponse updateTransaction(Long transactionId, TransactionUpdateRequest body) {
        return transactionService.updateTransaction(transactionId, body);
    }

    @Override
    public ResponseEntity<Void> deleteTransaction(Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }
}
