package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.time.LocalDate;
import java.util.List;

public class FrequencyBasedFraudDetection implements FraudDetectionStrategy {
    private static final int SUSPICIOUS_FREQUENCY_THRESHOLD = 10;
    public List<Transaction> recentTransactions;

    public FrequencyBasedFraudDetection(List<Transaction> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }

    @Override
    public boolean isSuspicious(Transaction transaction, String userId) {
        if (recentTransactions == null) return false;
        LocalDate today = LocalDate.now();
        long todayCount = recentTransactions.stream()
                .filter(t -> t.getSender().equals(userId))
                .filter(t -> t.getDate().equals(today))
                .count();
        return todayCount >= SUSPICIOUS_FREQUENCY_THRESHOLD;
    }
}

