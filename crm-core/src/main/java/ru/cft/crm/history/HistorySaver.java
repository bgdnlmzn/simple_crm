package ru.cft.crm.history;

import ru.cft.crm.entity.Seller;
import ru.cft.crm.entity.Transaction;
import ru.cft.crm.type.ChangeType;

public interface HistorySaver {
    void saveSellerHistory(Seller seller, ChangeType changeType);

    void saveTransactionHistory(Transaction transaction, ChangeType changeType);
}
