package com.example.national_bank_of_egypt.Repository;

import com.example.national_bank_of_egypt.Exceptions.RepositoryException;
import com.example.national_bank_of_egypt.Models.DataBaseDriver;
import com.example.national_bank_of_egypt.Models.Transaction;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionRepositoryImpl implements TransactionRepository {
    private final DataBaseDriver dataBaseDriver;

    public TransactionRepositoryImpl(DataBaseDriver dataBaseDriver) {
        this.dataBaseDriver = dataBaseDriver;
    }

    @Override
    public boolean save(Transaction transaction) {
        return dataBaseDriver.createTransaction(
            transaction.getTransactionId(),
            transaction.getSender(),
            transaction.getReceiver(),
            transaction.getSenderAccount(),
            transaction.getReceiverAccount(),
            transaction.getAmount(),
            transaction.getDate(),
            transaction.getMessage(),
            transaction.getStatus(),
            transaction.getTransactionType()
        );
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        try {
            ResultSet rs = dataBaseDriver.getTransactionById(transactionId);
            if (rs != null && rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getString("TransactionID"),
                    rs.getString("Sender"),
                    rs.getString("Receiver"),
                    rs.getString("SenderAccount"),
                    rs.getString("ReceiverAccount"),
                    rs.getDouble("Amount"),
                    LocalDate.parse(rs.getString("Date")),
                    rs.getString("Message"),
                    rs.getString("Status"),
                    rs.getString("TransactionType")
                );
                rs.close();
                return Optional.of(transaction);
            }
        } catch (Exception e) {
            throw new RepositoryException("Failed to find transaction by ID: " + transactionId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findByUserId(String userId, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            ResultSet rs = dataBaseDriver.getTransactions(userId, limit);
            if (rs != null) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                        rs.getString("TransactionID"),
                        rs.getString("Sender"),
                        rs.getString("Receiver"),
                        rs.getString("SenderAccount"),
                        rs.getString("ReceiverAccount"),
                        rs.getDouble("Amount"),
                        LocalDate.parse(rs.getString("Date")),
                        rs.getString("Message"),
                        rs.getString("Status"),
                        rs.getString("TransactionType")
                    ));
                }
                rs.close();
            }
        } catch (Exception e) {
            throw new RepositoryException("Failed to find transactions for user: " + userId, e);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findBySender(String sender) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            ResultSet rs = dataBaseDriver.getTransactions(sender, -1);
            if (rs != null) {
                while (rs.next()) {
                    if (rs.getString("Sender").equals(sender)) {
                        transactions.add(new Transaction(
                            rs.getString("TransactionID"),
                            rs.getString("Sender"),
                            rs.getString("Receiver"),
                            rs.getString("SenderAccount"),
                            rs.getString("ReceiverAccount"),
                            rs.getDouble("Amount"),
                            LocalDate.parse(rs.getString("Date")),
                            rs.getString("Message"),
                            rs.getString("Status"),
                            rs.getString("TransactionType")
                        ));
                    }
                }
                rs.close();
            }
        } catch (Exception e) {
            throw new RepositoryException("Failed to find transactions by sender: " + sender, e);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByReceiver(String receiver) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            ResultSet rs = dataBaseDriver.getTransactions(receiver, -1);
            if (rs != null) {
                while (rs.next()) {
                    if (rs.getString("Receiver").equals(receiver)) {
                        transactions.add(new Transaction(
                            rs.getString("TransactionID"),
                            rs.getString("Sender"),
                            rs.getString("Receiver"),
                            rs.getString("SenderAccount"),
                            rs.getString("ReceiverAccount"),
                            rs.getDouble("Amount"),
                            LocalDate.parse(rs.getString("Date")),
                            rs.getString("Message"),
                            rs.getString("Status"),
                            rs.getString("TransactionType")
                        ));
                    }
                }
                rs.close();
            }
        } catch (Exception e) {
            throw new RepositoryException("Failed to find transactions by receiver: " + receiver, e);
        }
        return transactions;
    }

    @Override
    public boolean updateStatus(String transactionId, String status) {
        return dataBaseDriver.updateTransactionStatus(transactionId, status);
    }
}
