package ru.cft.crm.mapper;

import lombok.experimental.UtilityClass;
import ru.cft.crm.model.transaction.TransactionCreateRequest;
import ru.cft.crm.model.transaction.TransactionResponse;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.type.PaymentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class TransactionMapper {
    public static Transaction mapRequestToTransaction(
            TransactionCreateRequest body,
            Seller seller,
            PaymentType paymentType) {
        Transaction transaction = new Transaction();

        transaction.setSeller(seller);
        transaction.setAmount(body.amount());
        transaction.setPaymentType(paymentType);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setIsActive(true);

        return transaction;
    }

    public static TransactionResponse mapTransactionToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getSeller().getId(),
                transaction.getAmount(),
                transaction.getPaymentType(),
                transaction.getTransactionDate(),
                transaction.getUpdatedAt(),
                transaction.getIsActive()
        );
    }

    public static List<TransactionResponse> mapTransactionsToResponses(
            List<Transaction> transactions) {
        return transactions.stream()
                .map(TransactionMapper::mapTransactionToResponse)
                .collect(Collectors.toList());
    }
}
