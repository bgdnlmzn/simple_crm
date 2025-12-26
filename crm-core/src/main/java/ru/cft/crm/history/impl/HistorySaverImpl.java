package ru.cft.crm.history.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.SellersHistory;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.entity.TransactionsHistory;
import ru.cft.crm.history.HistorySaver;
import ru.cft.crm.repository.SellersHistoryRepository;
import ru.cft.crm.repository.TransactionsHistoryRepository;
import ru.cft.crm.type.ChangeType;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class HistorySaverImpl implements HistorySaver {

    private final SellersHistoryRepository sellersHistoryRepository;

    private final TransactionsHistoryRepository transactionsHistoryRepository;

    @Override
    public void saveSellerHistory(Seller seller, ChangeType changeType) {
        SellersHistory history = new SellersHistory();

        history.setSellerId(seller.getId());
        history.setSellerName(seller.getSellerName());
        history.setContactInfo(seller.getContactInfo());
        history.setRegistrationDate(seller.getRegistrationDate());
        history.setUpdatedAt(seller.getUpdatedAt());
        history.setChangeTimestamp(LocalDateTime.now());
        history.setChangeType(changeType);

        sellersHistoryRepository.save(history);
    }

    @Override
    public void saveTransactionHistory(Transaction transaction, ChangeType changeType) {
        TransactionsHistory history = new TransactionsHistory();

        history.setTransactionId(transaction.getId());
        history.setSellerId(transaction.getSeller().getId());
        history.setAmount(transaction.getAmount());
        history.setPaymentType(transaction.getPaymentType());
        history.setTransactionDate(transaction.getTransactionDate());
        history.setUpdatedAt(transaction.getUpdatedAt());
        history.setChangeTimestamp(LocalDateTime.now());
        history.setChangeType(changeType);

        transactionsHistoryRepository.save(history);
    }
}
