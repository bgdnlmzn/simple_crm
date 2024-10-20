package ru.cft.crm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cft.crm.dto.transaction.TransactionCreateRequest;
import ru.cft.crm.dto.transaction.TransactionResponse;
import ru.cft.crm.dto.transaction.TransactionUpdateRequest;
import ru.cft.crm.service.crud.TransactionService;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transactions")
    public TransactionResponse createTransaction(
            @RequestBody @Valid TransactionCreateRequest body) {
        return transactionService.createTransaction(body);
    }

    @GetMapping("/transactions/{transactionId}")
    public TransactionResponse getTransaction(@PathVariable Long transactionId) {
        return transactionService.getTransaction(transactionId);
    }

    @GetMapping("/transactions/seller/{sellerId}")
    public List<TransactionResponse> getAllSellersTransactions(
            @PathVariable Long sellerId) {
        return transactionService.getAllSellersTransactions(sellerId);
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> getAllActiveTransactions() {
        return transactionService.getAllActiveTransactions();
    }

    @GetMapping("/transactions/all")
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @PatchMapping("/transactions/{transactionId}")
    public TransactionResponse updateTransaction(
            @PathVariable Long transactionId,
            @RequestBody @Valid TransactionUpdateRequest body) {
        return transactionService.updateTransaction(transactionId, body);
    }

    @DeleteMapping("/transactions/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }
}
