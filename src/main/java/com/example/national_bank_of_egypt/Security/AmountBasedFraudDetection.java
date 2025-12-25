package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.List;

/**
 * Amount-Based Fraud Detection Strategy.
 * 
 * <p>This strategy detects potentially fraudulent transactions based on the
 * transaction amount. Large transactions that exceed a configurable threshold
 * are flagged as suspicious.</p>
 * 
 * <h2>Design Pattern: Strategy Pattern (Concrete Strategy)</h2>
 * <p>Implements both {@link FraudDetectionStrategy} for binary detection and
 * {@link RiskBasedFraudDetectionStrategy} for risk-scored assessment.</p>
 * 
 * <h2>Detection Logic:</h2>
 * <table border="1">
 *   <tr><th>Condition</th><th>Risk Points</th><th>Description</th></tr>
 *   <tr><td>Amount > $10,000</td><td>30</td><td>Large transaction exceeds threshold</td></tr>
 *   <tr><td>Amount > $8,000 (80%)</td><td>15</td><td>Approaching threshold warning</td></tr>
 *   <tr><td>Amount ≤ $8,000</td><td>0</td><td>Within normal range</td></tr>
 * </table>
 * 
 * <h2>Rationale:</h2>
 * <p>Large transactions are commonly associated with fraud attempts such as:
 * <ul>
 *   <li>Account takeover attempts to drain funds</li>
 *   <li>Money laundering activities</li>
 *   <li>Unauthorized large purchases</li>
 * </ul>
 * </p>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see FraudDetectionStrategy
 * @see RiskBasedFraudDetectionStrategy
 */
public class AmountBasedFraudDetection implements FraudDetectionStrategy, RiskBasedFraudDetectionStrategy {
    
    /**
     * Threshold amount in dollars above which transactions are considered suspicious.
     * Transactions exceeding this amount receive the highest risk points.
     */
    private static final double SUSPICIOUS_AMOUNT_THRESHOLD = 10000.0;
    
    /**
     * Binary fraud detection method for backward compatibility.
     * 
     * <p>Simply checks if the transaction amount exceeds the threshold.</p>
     * 
     * @param transaction The transaction to analyze
     * @param userId The user performing the transaction (not used in amount-based detection)
     * @return {@code true} if amount exceeds $10,000, {@code false} otherwise
     */
    @Override
    public boolean isSuspicious(Transaction transaction, String userId) {
        return transaction.getAmount() > SUSPICIOUS_AMOUNT_THRESHOLD;
    }
    
    /**
     * Risk-based fraud assessment method.
     * 
     * <p>Provides graduated risk scoring based on transaction amount:
     * <ul>
     *   <li>Above threshold ($10,000): 30 risk points</li>
     *   <li>80-100% of threshold ($8,000-$10,000): 15 risk points</li>
     *   <li>Below 80% of threshold: No risk points added</li>
     * </ul>
     * </p>
     * 
     * @param transaction The transaction to analyze
     * @param userId The user performing the transaction (not used in amount-based detection)
     * @param recentTransactions Not used by this strategy
     * @param builder The Builder object to add risk factors to
     */
    @Override
    public void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult.Builder builder) {
        double amount = transaction.getAmount();
        
        // Check if amount exceeds the suspicious threshold
        if (amount > SUSPICIOUS_AMOUNT_THRESHOLD) {
            builder.addRiskFactor(
                "AMOUNT",
                30,
                String.format("Large transaction amount: $%.2f (exceeds threshold of $%.2f)", 
                    amount, SUSPICIOUS_AMOUNT_THRESHOLD)
            );
        // Check if amount is approaching the threshold (80% or more)
        } else if (amount > SUSPICIOUS_AMOUNT_THRESHOLD * 0.8) {
            builder.addRiskFactor(
                "AMOUNT",
                15,
                String.format("Approaching threshold: $%.2f (80%% of $%.2f threshold)", 
                    amount, SUSPICIOUS_AMOUNT_THRESHOLD)
            );
        }
        // Amounts below 80% of threshold do not add any risk points
    }
}

