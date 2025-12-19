package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.List;
import java.util.stream.Collectors;

public class AmountPatternDetection implements RiskBasedFraudDetectionStrategy {
    private static final double SUSPICIOUS_MULTIPLIER = 3.0;
    
    @Override
    public void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult result) {
        if (recentTransactions == null || recentTransactions.isEmpty()) {
            return;
        }
        
        List<Transaction> userTransactions = recentTransactions.stream()
            .filter(t -> t.getSender().equals(userId))
            .collect(Collectors.toList());
        
        if (userTransactions.isEmpty()) {
            return;
        }
        
        double averageAmount = userTransactions.stream()
            .mapToDouble(Transaction::getAmount)
            .average()
            .orElse(0.0);
        
        double maxAmount = userTransactions.stream()
            .mapToDouble(Transaction::getAmount)
            .max()
            .orElse(0.0);
        
        double currentAmount = transaction.getAmount();
        
        if (averageAmount > 0 && currentAmount > averageAmount * SUSPICIOUS_MULTIPLIER) {
            result.addRiskFactor(
                "AMOUNT_PATTERN",
                25,
                String.format("Amount ($%.2f) is %.1fx user's average ($%.2f)", 
                    currentAmount, currentAmount / averageAmount, averageAmount)
            );
        } else if (maxAmount > 0 && currentAmount > maxAmount * 2.0) {
            result.addRiskFactor(
                "AMOUNT_PATTERN",
                20,
                String.format("Amount ($%.2f) is 2x user's largest transaction ($%.2f)", 
                    currentAmount, maxAmount)
            );
        }
    }
}
