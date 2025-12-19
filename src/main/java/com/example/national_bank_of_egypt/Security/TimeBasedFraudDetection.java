package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.time.LocalTime;
import java.util.List;

public class TimeBasedFraudDetection implements RiskBasedFraudDetectionStrategy {
    private static final LocalTime BUSINESS_START = LocalTime.of(9, 0);
    private static final LocalTime BUSINESS_END = LocalTime.of(17, 0);
    private static final LocalTime UNUSUAL_START = LocalTime.of(22, 0);
    private static final LocalTime UNUSUAL_END = LocalTime.of(6, 0);
    
    @Override
    public void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult result) {
        LocalTime currentTime = LocalTime.now();
        
        boolean isBusinessHours = currentTime.isAfter(BUSINESS_START) && currentTime.isBefore(BUSINESS_END);
        boolean isUnusualHours = currentTime.isAfter(UNUSUAL_START) || currentTime.isBefore(UNUSUAL_END);
        
        if (isUnusualHours) {
            result.addRiskFactor(
                "TIME",
                15,
                String.format("Transaction at unusual time: %s (outside normal business hours)", currentTime)
            );
        } else if (!isBusinessHours) {
            result.addRiskFactor(
                "TIME",
                10,
                String.format("Transaction outside business hours: %s", currentTime)
            );
        }
    }
}
