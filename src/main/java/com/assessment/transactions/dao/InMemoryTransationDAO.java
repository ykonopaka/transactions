package com.assessment.transactions.dao;

import com.assessment.transactions.model.Transaction;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.assessment.transactions.model.Status;
import com.assessment.transactions.model.TransactionType;
import org.junit.Test;

import java.util.*;

/**
 * Created by ykono on 17.09.2016.
 */
public class InMemoryTransationDAO implements TransactionDAO {
    private Table<Long, TransactionType, Transaction> transactionTable = HashBasedTable.create();
    private Table<Long, Long, Transaction> relationTable = HashBasedTable.create();

    @Override
    public Status store(Transaction transaction) {
        if (!exists(transaction.getId())) {
            transactionTable.put(transaction.getId(), transaction.getTransactionType(), transaction);
            if (transaction.getParentId() != null)    {
                relationTable.put(transaction.getId(), transaction.getParentId(), transaction);
            }
            return Status.OK;
        }

        return Status.FAILED;
    }

    @Override
    public Optional<Transaction> getTransaction(Long id) {
        List<Transaction> result = new ArrayList<>(transactionTable.row(id).values());
        if (result.size() > 1)  {
            throw new RuntimeException();
        } else if (result.size() == 0)  {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Transaction> getTransactionsByType(TransactionType transactionType) {
        return new ArrayList<>(transactionTable.column(transactionType).values());
    }

    @Override
    public Double getSum(Long transactionId) {
        Optional<Transaction> transactionOptional = getTransaction(transactionId);
        double result = 0;
        if (transactionOptional.isPresent())    {
            result = getTransaction(transactionId).get().getAmount() +
                    getChildren(transactionId).parallelStream().
                            mapToDouble( tr -> getSum(tr.getParentId())).sum();
        }
        return result;
    }


    private List<Transaction> getChildren(Long transactionId)   {
        return new ArrayList<>(relationTable.row(transactionId).values());
    }

    private boolean exists(Long id)    {
        return transactionTable.containsRow(id);
    }

    @Test
    public void BasicOneTransactionTest()   {
        TransactionDAO transactionDAO = new InMemoryTransationDAO();
        Transaction tr1 = new Transaction(1L, TransactionType.TYPE_1, 0.1D, null);

        transactionDAO.store(tr1);

        System.out.println();

    }
}
