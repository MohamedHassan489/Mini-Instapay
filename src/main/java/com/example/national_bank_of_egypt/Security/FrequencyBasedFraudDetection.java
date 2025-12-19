package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.time.LocalDate;
import java.util.List;

public class FrequencyBasedFraudDetection implements FraudDetectionStrategy, RiskBasedFraudDetectionStrategy {
    private static final int SUSPICIOUS_FREQUENCY_THRESHOLD = 10;
    public List<Transaction> recentTransactions;

    public FrequencyBasedFraudDetection(List<Transaction> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
    
    public FrequencyBasedFraudDetection() {
        this.recentTransactions = null;
    }

    // Old binary method (for backward compatibility)
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
    
    // New risk-based method
    @Override
    public void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult result) {
        if (recentTransactions == null || recentTransactions.isEmpty()) {
            return;
        }
        
        LocalDate today = LocalDate.now();
        long todayCount = recentTransactions.stream()
            .filter(t -> t.getSender().equals(userId))
            .filter(t -> t.getDate().equals(today))
            .count();
        
        if (todayCount >= SUSPICIOUS_FREQUENCY_THRESHOLD) {
            result.addRiskFactor(
                "FREQUENCY",
                25,
                String.format("High transaction frequency: %d transactions today (threshold: %d)", 
                    todayCount, SUSPICIOUS_FREQUENCY_THRESHOLD)
            );
        } else if (todayCount >= SUSPICIOUS_FREQUENCY_THRESHOLD * 0.8) {
            result.addRiskFactor(
                "FREQUENCY",
                15,
                String.format("Approaching frequency limit: %d transactions today", todayCount)
            );
        }
    }
}

