package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.ArrayList;
import java.util.List;

public class FraudDetectionService {
    private static FraudDetectionService instance;
    private final List<FraudDetectionStrategy> strategies;

    private FraudDetectionService() {
        this.strategies = new ArrayList<>();
        this.strategies.add(new AmountBasedFraudDetection());
    }

    public static synchronized FraudDetectionService getInstance() {
        if (instance == null) {
            instance = new FraudDetectionService();
        }
        return instance;
    }

    public void addStrategy(FraudDetectionStrategy strategy) {
        strategies.add(strategy);
    }

    public boolean detectFraud(Transaction transaction, String userId, List<Transaction> recentTransactions) {
        for (FraudDetectionStrategy strategy : strategies) {
            if (strategy instanceof FrequencyBasedFraudDetection) {
                FrequencyBasedFraudDetection freqStrategy = (FrequencyBasedFraudDetection) strategy;
                freqStrategy.recentTransactions = recentTransactions;
            }
            if (strategy.isSuspicious(transaction, userId)) {
                return true;
            }
        }
        return false;
    }
}

