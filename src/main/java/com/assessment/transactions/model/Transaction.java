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

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", parentId=" + parentId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (!getId().equals(that.getId())) return false;
        if (getTransactionType() != that.getTransactionType()) return false;
        if (!getAmount().equals(that.getAmount())) return false;
        return getParentId().equals(that.getParentId());

    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getTransactionType().hashCode();
        result = 31 * result + getAmount().hashCode();
        result = 31 * result + getParentId().hashCode();
        return result;
    }
}
