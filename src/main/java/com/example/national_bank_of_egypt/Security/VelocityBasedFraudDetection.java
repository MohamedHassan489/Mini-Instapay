package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.List;

public class VelocityBasedFraudDetection implements RiskBasedFraudDetectionStrategy {
    // VELOCITY_WINDOW_MINUTES reserved for future timestamp-based velocity detection
    // Currently using same-day transactions as velocity indicator
    @SuppressWarnings("unused")
    private static final int VELOCITY_WINDOW_MINUTES = 5;
    private static final int SUSPICIOUS_VELOCITY_THRESHOLD = 3;
    
    @Override
    public void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult result) {
        if (recentTransactions == null || recentTransactions.isEmpty()) {
            return;
        }
        
        // Count transactions from today (as proxy for recent activity)
        // Since Transaction only stores LocalDate, we use same-day transactions as velocity indicator
        java.time.LocalDate today = java.time.LocalDate.now();
        long todayCount = recentTransactions.stream()
            .filter(t -> t.getSender().equals(userId))
            .filter(t -> t.getDate().equals(today))
            .count();
        
        // If user has made multiple transactions today, it indicates high velocity
        // This is a practical approximation since we don't have exact timestamps
        if (todayCount >= SUSPICIOUS_VELOCITY_THRESHOLD) {
            result.addRiskFactor(
                "VELOCITY",
                30,
                String.format("Unusually rapid transactions: %d transactions today (indicates high velocity)", 
                    todayCount)
            );
        } else if (todayCount >= 2) {
            result.addRiskFactor(
                "VELOCITY",
                15,
                String.format("Multiple transactions today: %d transactions (may indicate rapid activity)", 
                    todayCount)
            );
        }
    }
}
