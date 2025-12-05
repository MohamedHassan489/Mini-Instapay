package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;

public class AmountBasedFraudDetection implements FraudDetectionStrategy {
    private static final double SUSPICIOUS_AMOUNT_THRESHOLD = 10000.0;

    @Override
    public boolean isSuspicious(Transaction transaction, String userId) {
        return transaction.getAmount() > SUSPICIOUS_AMOUNT_THRESHOLD;
    }
}

