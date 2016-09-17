package com.assessment.transactions.dao;

import com.assessment.transactions.model.Transaction;
import com.assessment.transactions.model.Status;
import com.assessment.transactions.model.TransactionType;

import java.util.List;
import java.util.Optional;

/**
 * Created by ykono on 17.09.2016.
 */
public interface TransactionDAO {
    Status store(Transaction transaction);
    Optional<Transaction> getTransaction(Long id);
    List<Transaction> getTransactionsByType(TransactionType transactionType);
    Double getSum(Long id);
}
