package com.assessment.transactions.model;

/**
 * Created by ykono on 17.09.2016.
 */
public class Transaction {
    private final Long id;
    private final TransactionType transactionType;
    private final Double amount;
    private final Long parentId;

    public Transaction(Long id, TransactionType transactionType, Double amount, Long parentId) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public Long getParentId() {
        return parentId;
    }
}
