package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.List;

public interface RiskBasedFraudDetectionStrategy {
    void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult result);
}
