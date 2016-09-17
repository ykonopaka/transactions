package com.assessment.transactions.dao;

import com.assessment.transactions.model.Transaction;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.assessment.transactions.model.Status;
import com.assessment.transactions.model.TransactionType;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by ykono on 17.09.2016.
 */
public class InMemoryTransationDAO implements TransactionDAO {
    private Table<Long, TransactionType, Transaction> transactionTable = HashBasedTable.create();
    private Table<Long, Long, Transaction> relationTable = HashBasedTable.create();

    @Override
    public synchronized Status store(Transaction transaction) {
        if (!exists(transaction.getId())) {
            transactionTable.put(transaction.getId(), transaction.getTransactionType(), transaction);
            if (transaction.getParentId() != null) {
                relationTable.put(transaction.getParentId(), transaction.getId(), transaction);
            }
            return Status.OK;
        }

        return Status.FAILED;
    }

    @Override
    public synchronized Optional<Transaction> getTransaction(Long id) {
        List<Transaction> result = new ArrayList<>(transactionTable.row(id).values());
        if (result.size() > 1) {
            throw new RuntimeException();
        } else if (result.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public synchronized List<Transaction> getTransactionsByType(TransactionType transactionType) {
        return new ArrayList<>(transactionTable.column(transactionType).values());
    }

    @Override
    public synchronized Double getSum(Long transactionId) {
        Optional<Transaction> transactionOptional = getTransaction(transactionId);
        double result = 0;
        if (transactionOptional.isPresent()) {
            result = transactionOptional.get().getAmount();
            List<Transaction> children = getChildren(transactionId);

            if (children.size() > 0) {
                result = result + children.stream().mapToDouble(tr -> getSum(tr.getId())).sum();
            }
        }
        return result;
    }

    private synchronized List<Transaction> getChildren(Long transactionId) {
        return new ArrayList<>(relationTable.row(transactionId).values());
    }

    private synchronized boolean exists(Long id) {
        return transactionTable.containsRow(id);
    }

    @Test
    public void basicOneTransactionTest() {
        TransactionDAO transactionDAO = new InMemoryTransationDAO();
        Transaction tr1 = new Transaction(1L, TransactionType.TYPE_1, 0.1D, null);
        transactionDAO.store(tr1);
        Assert.assertEquals(tr1, transactionDAO.getTransaction(1L).get());
    }

    @Test
    public void basicGetTransactionByType() {
        TransactionDAO transactionDAO = new InMemoryTransationDAO();
        Transaction tr1 = new Transaction(1L, TransactionType.TYPE_1, 0.1D, null);
        Transaction tr2 = new Transaction(2L, TransactionType.TYPE_1, 0.2D, null);
        Transaction tr3 = new Transaction(3L, TransactionType.TYPE_2, 0.2D, null);
        transactionDAO.store(tr1);
        transactionDAO.store(tr2);
        transactionDAO.store(tr3);

        List<Transaction> transactionAct = transactionDAO.getTransactionsByType(TransactionType.TYPE_1);
        List<Transaction> transactionExp = Arrays.asList(tr1, tr2);

        Assert.assertEquals(transactionExp, transactionAct);
    }

    @Test
    public void basicGetSum() {
        TransactionDAO transactionDAO = new InMemoryTransationDAO();
        Transaction tr1 = new Transaction(1L, TransactionType.TYPE_1, 0.1D, null);
        Transaction tr2 = new Transaction(2L, TransactionType.TYPE_1, 0.2D, null);
        Transaction tr3 = new Transaction(3L, TransactionType.TYPE_2, 0.2D, null);
        transactionDAO.store(tr1);
        transactionDAO.store(tr2);
        transactionDAO.store(tr3);

        transactionDAO.getSum(1L);

        Assert.assertEquals(0.1D, transactionDAO.getSum(1L), 0);
    }

    @Test
    public void basicGetSumParent() {
        TransactionDAO transactionDAO = new InMemoryTransationDAO();
        Transaction tr1 = new Transaction(1L, TransactionType.TYPE_1, 0.1D, null);
        Transaction tr2 = new Transaction(2L, TransactionType.TYPE_1, 0.2D, 1L);
        Transaction tr3 = new Transaction(3L, TransactionType.TYPE_2, 0.3D, 1L);
        Transaction tr4 = new Transaction(3L, TransactionType.TYPE_2, 0.4D, null);
        transactionDAO.store(tr1);
        transactionDAO.store(tr2);
        transactionDAO.store(tr3);
        transactionDAO.store(tr4);

        transactionDAO.getSum(1L);

        Assert.assertEquals(0.6D, transactionDAO.getSum(1L), 0);
    }
}
