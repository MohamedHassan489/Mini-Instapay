package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;

public interface FraudDetectionStrategy {
    boolean isSuspicious(Transaction transaction, String userId);
}

