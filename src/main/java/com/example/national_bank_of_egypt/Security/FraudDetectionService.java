package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.ArrayList;
import java.util.List;

public class FraudDetectionService {
    private static FraudDetectionService instance;
    private final List<FraudDetectionStrategy> strategies;
    private final List<RiskBasedFraudDetectionStrategy> riskStrategies;

    private FraudDetectionService() {
        this.strategies = new ArrayList<>();
        this.riskStrategies = new ArrayList<>();
        
        // Initialize default strategies
        this.strategies.add(new AmountBasedFraudDetection());
        
        // Initialize risk-based strategies
        this.riskStrategies.add(new AmountBasedFraudDetection());
        this.riskStrategies.add(new VelocityBasedFraudDetection());
        this.riskStrategies.add(new RecipientPatternDetection());
        this.riskStrategies.add(new AmountPatternDetection());
        this.riskStrategies.add(new TimeBasedFraudDetection());
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
    
    public void addRiskStrategy(RiskBasedFraudDetectionStrategy strategy) {
        riskStrategies.add(strategy);
    }

    // Old binary method (for backward compatibility)
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
    
    // New risk-based assessment method
    public FraudRiskResult assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions) {
        FraudRiskResult result = new FraudRiskResult();
        
        // Add frequency-based strategy dynamically if we have recent transactions
        if (recentTransactions != null && !recentTransactions.isEmpty()) {
            FrequencyBasedFraudDetection freqStrategy = new FrequencyBasedFraudDetection(recentTransactions);
            freqStrategy.assessRisk(transaction, userId, recentTransactions, result);
        }
        
        // Run all risk-based strategies
        for (RiskBasedFraudDetectionStrategy strategy : riskStrategies) {
            try {
                strategy.assessRisk(transaction, userId, recentTransactions, result);
            } catch (Exception e) {
                // Log error but continue with other strategies
                e.printStackTrace();
            }
        }
        
        return result;
    }
}

