package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.List;

public class AmountBasedFraudDetection implements FraudDetectionStrategy, RiskBasedFraudDetectionStrategy {
    private static final double SUSPICIOUS_AMOUNT_THRESHOLD = 10000.0;
    
    // Old binary method (for backward compatibility)
    @Override
    public boolean isSuspicious(Transaction transaction, String userId) {
        return transaction.getAmount() > SUSPICIOUS_AMOUNT_THRESHOLD;
    }
    
    // New risk-based method
    @Override
    public void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult result) {
        double amount = transaction.getAmount();
        
        if (amount > SUSPICIOUS_AMOUNT_THRESHOLD) {
            result.addRiskFactor(
                "AMOUNT",
                30,
                String.format("Large transaction amount: $%.2f (exceeds threshold of $%.2f)", 
                    amount, SUSPICIOUS_AMOUNT_THRESHOLD)
            );
        } else if (amount > SUSPICIOUS_AMOUNT_THRESHOLD * 0.8) {
            result.addRiskFactor(
                "AMOUNT",
                15,
                String.format("Approaching threshold: $%.2f (80%% of $%.2f threshold)", 
                    amount, SUSPICIOUS_AMOUNT_THRESHOLD)
            );
        }
    }
}

