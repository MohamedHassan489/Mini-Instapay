package com.example.national_bank_of_egypt.Repository;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    boolean save(Transaction transaction);
    Optional<Transaction> findById(String transactionId);
    List<Transaction> findByUserId(String userId, int limit);
    List<Transaction> findBySender(String sender);
    List<Transaction> findByReceiver(String receiver);
    boolean updateStatus(String transactionId, String status);
}

