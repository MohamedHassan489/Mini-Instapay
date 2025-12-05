package com.example.national_bank_of_egypt.Transactions;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionFactory {
    public static Transaction createInstantTransaction(String sender, String receiver,
                                                      String senderAccount, String receiverAccount,
                                                      double amount, String message) {
        String transactionId = UUID.randomUUID().toString();
        return new Transaction(
            transactionId, sender, receiver, senderAccount, receiverAccount,
            amount, LocalDate.now(), message, "PENDING", "INSTANT"
        );
    }

    public static Transaction createScheduledTransaction(String sender, String receiver,
                                                        String senderAccount, String receiverAccount,
                                                        double amount, LocalDate scheduledDate, String message) {
        String transactionId = UUID.randomUUID().toString();
        return new Transaction(
            transactionId, sender, receiver, senderAccount, receiverAccount,
            amount, scheduledDate, message, "SCHEDULED", "SCHEDULED"
        );
    }

    public static Transaction createRefundTransaction(String originalTransactionId, String sender, String receiver,
                                                      String senderAccount, String receiverAccount,
                                                      double amount, String message) {
        String transactionId = UUID.randomUUID().toString();
        return new Transaction(
            transactionId, sender, receiver, senderAccount, receiverAccount,
            amount, LocalDate.now(), "Refund for: " + message, "PENDING", "REFUND"
        );
    }
}

